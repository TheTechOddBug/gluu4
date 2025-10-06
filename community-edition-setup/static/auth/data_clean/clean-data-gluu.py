#!/usr/bin/python3

import os
import sys
import time
import shutil
import logging
import argparse
import datetime
import subprocess
import configparser
import requests
import urllib3
import ldap3


from logging.handlers import RotatingFileHandler
from requests.auth import HTTPBasicAuth

urllib3.disable_warnings()

parser = argparse.ArgumentParser(description="This script removes Gluu Server tokens")
parser.add_argument('-limit', help="Limit to delete entry per execution", type=int, default=1000)
parser.add_argument('--yes', help="For execute without prompt", action='store_true')
parser.add_argument('--offset', help="Time offset for deleting entries in seconds",  type=int, default=0)

argsp = parser.parse_args()

cleaner_dir = '/opt/gluu/data-cleaner'
log_dir = os.path.join(cleaner_dir, 'logs')
cleaner_tmp_dir = os.path.join(cleaner_dir, 'tmp')
cmd_fn = os.path.join(cleaner_tmp_dir, f'data-clean-{os.urandom(8).hex()}.sql')
time_offset = argsp.offset


if not os.path.exists(log_dir):
    os.makedirs(log_dir)

if not os.path.exists(cleaner_tmp_dir):
    os.makedirs(cleaner_tmp_dir)

my_logger = logging.getLogger('Gluu Data Cleaner')
my_logger.setLevel(logging.DEBUG)
handler = RotatingFileHandler(os.path.join(log_dir, 'data-clean.log'), maxBytes=50*1024*1024, backupCount=10)
formatter=logging.Formatter('%(asctime)s %(levelname)s\t%(message)s')
handler.setFormatter(formatter)
my_logger.addHandler(handler)


config = configparser.ConfigParser()
config_fn = os.path.join(cleaner_dir, 'data-clean.ini')
if not os.path.exists(config_fn):
    my_logger.error("Config file %s not found", config_fn)
    sys.exit()

try:
    config.read(config_fn)
    tables = config['main']['tables'].split()
except Exception as e:
    my_logger.error(e)
    sys.exit()

clnt_last_access_interval = config['main'].get('cleanUpInactiveClientAfterHoursOfInactivity')

if not argsp.yes:
    print(f"This command will remove first {argsp.limit} entires of the following tables where expiration is before than now")
    print(', '.join(tables))
    response = input("Are you sure you want to do this? Type yes to approve. ")
    if response != 'yes':
        print("Exiting without doing anyting...")
        sys.exit()

def read_prop(prop_fn):
    prop_dict = {}
    with open(prop_fn) as f:
        for l in f:
            nlist = []
            for sep in (':', '='):
                nsep = l.find(sep)
                if nsep > -1:
                    nlist.append(nsep)
            if nlist:
                n = min(nlist)
                key = l[:n].strip()
                val = l[n+1:].strip()
                prop_dict[key] = val

    return prop_dict


gluu_prop_fn = '/etc/gluu/conf/gluu{}.properties'

gluu_prop = read_prop(gluu_prop_fn.format(''))
persistence_type = gluu_prop['persistence.type']
persistence_prop = read_prop(gluu_prop_fn.format('-'+persistence_type))


class CBM:

    def __init__(self, host, admin, password):
        self.auth = HTTPBasicAuth(admin, password)
        self.n1ql_api = 'https://{}:18093/query/service'.format(host)

    def exec_query(self, query):
        my_logger.info("Executing n1ql {}".format(query))
        data = {'statement': query}
        verify_ssl = False
        result = requests.post(self.n1ql_api, data=data, auth=self.auth, verify=verify_ssl)
        my_logger.info("CB server response {}".format(result.json()))
        return result


def run_command(cmd, env):
    my_logger.info('Executing %s', cmd)

    output = subprocess.run(cmd, env=env, shell=True, capture_output=True)
    if output.stdout:
        my_logger.debug(output.stdout.decode())
    if output.stderr:
        my_logger.error(output.stderr.decode())

def decode(s):
    return os.popen(f'/opt/gluu/bin/encode.py -D {s}').read().strip()


if persistence_type == 'sql':
    connection_uri_list = persistence_prop['connection.uri'].split(':')
    db_type = connection_uri_list[1]
    db_name = connection_uri_list[3].split('/')[1].strip().split('?')[0]
    db_host = connection_uri_list[2].strip('/')
    db_port = connection_uri_list[3].split('/')[0].strip()
    db_user = persistence_prop['auth.userName']
    db_user_pw_enc = persistence_prop['auth.userPassword']
    db_user_pw = decode(db_user_pw_enc)

    if db_type == 'mysql':
        mysql_cmd = shutil.which('mysql')
        cmd = f'{mysql_cmd} -vv --user={db_user} --host={db_host} --port={db_port} {db_name} < {cmd_fn}'

        with open(cmd_fn, 'w') as w:
            for table in tables:
                sql_query = f'''DELETE FROM {table} WHERE del=TRUE AND exp < NOW() LIMIT {argsp.limit};\n'''
                w.write(sql_query)
                if table == 'oxAuthClient' and clnt_last_access_interval:
                    sql_query = f'''DELETE FROM {table} WHERE del=TRUE AND oxLastAccessTime < DATE_SUB(NOW(), INTERVAL {clnt_last_access_interval} HOUR) LIMIT {argsp.limit};\n'''
                    w.write(sql_query)
        run_command(cmd, env={'MYSQL_PWD': db_user_pw})
        os.remove(cmd_fn)

    elif db_type == 'postgresql':
        with open(cmd_fn, 'w') as w:
            pgsql_cmd = shutil.which('psql')
            cmd = f'{pgsql_cmd} -a -b -e --user={db_user} --host={db_host} --port={db_port} --dbname={db_name} -f {cmd_fn}'
            for table in tables:
                sql_query = f'''DELETE FROM "{table}" WHERE "doc_id" IN (SELECT "doc_id" FROM "{table}" WHERE "del"=TRUE and "exp" < NOW() LIMIT {argsp.limit});\n'''
                w.write(sql_query)
                if table == 'oxAuthClient' and clnt_last_access_interval:
                    sql_query = f'''DELETE FROM "{table}" WHERE "doc_id" IN (SELECT "doc_id" FROM "{table}" WHERE "del"=TRUE and "oxLastAccessTime" < (NOW() - INTERVAL '{clnt_last_access_interval}' HOUR) LIMIT {argsp.limit});\n'''
                    w.write(sql_query)

        run_command(cmd, env={'PGPASSWORD': db_user_pw})
        os.remove(cmd_fn)

    else:
        sys.stderr.write(f"Database {db_type} is not supported by this script.\n")


elif persistence_type == 'ldap':
    ldap_host, ldap_port = persistence_prop['servers'].split(',')[0].split(':')
    ldap_password = decode(persistence_prop['bindPassword'])
    server = ldap3.Server(ldap_host, port=int(ldap_port), use_ssl=True)
    ldap_conn = ldap3.Connection(server, user=persistence_prop['bindDN'], password=ldap_password)
    ldap_conn.bind()

    utc_time = datetime.datetime.utcnow() - datetime.timedelta(seconds=time_offset)
    del_time = '{}{:02d}{:02d}{:02d}{:02d}{:02d}.{}Z'.format(utc_time.year, utc_time.month, utc_time.day, utc_time.hour, utc_time.minute, utc_time.second, str(utc_time.microsecond)[:3])

    for objcls in tables:
        lat = f'(oxLastAccessTime<={del_time})' if objcls == 'oxAuthClient' else ''
        search_filter = f'(&(objectClass={objcls})(del=true)(exp<={del_time}){lat})'
        my_logger.info(f"Searching with filter {search_filter}")

        cookie = True
        while cookie:
            ldap_conn.search(
                search_base='o=gluu',
                search_filter=search_filter,
                search_scope=ldap3.SUBTREE,
                attributes=[],
                paged_size=argsp.limit,
                paged_cookie=None if cookie is True else cookie
                )

            cookie = ldap_conn.result['controls']['1.2.840.113556.1.4.319']['value']['cookie']

            for entry in ldap_conn.response[:]:
                my_logger.info(f"  Deleting entry {entry['dn']}")
                ldap_conn.delete(entry['dn'])


elif persistence_type == 'couchbase':
    cb_host = persistence_prop['servers'].split(',')[0]
    cb_user = persistence_prop['auth.userName']
    cb_password_enc = persistence_prop['auth.userPassword']
    cb_password = decode(cb_password_enc)
    cbm = CBM(cb_host, cb_user, cb_password)
    del_time = (datetime.datetime.now() - datetime.timedelta(seconds=time_offset)).isoformat(timespec="milliseconds") + 'Z'
    for bucket in ('gluu_session', 'gluu_token', 'gluu_cache'):
        cbm.exec_query(f'DELETE FROM `{bucket}` WHERE `exp` < "{del_time}" AND `del`=true LIMIT {argsp.limit}')


elif persistence_type == 'spanner':

    from spanner_rest_client import SpannerClient

    spanner_project = persistence_prop['connection.project']
    spanner_instance = persistence_prop['connection.instance']
    spanner_database = persistence_prop['connection.database']
    spanner_emulator_host = None
    google_application_credentials = None

    if 'connection.emulator-host' in persistence_prop:
        spanner_emulator_host = persistence_prop['connection.emulator-host'].split(':')[0]

    elif 'auth.credentials-file' in persistence_prop:
        google_application_credentials = persistence_prop['auth.credentials-file']

    spanner_client = SpannerClient(
                            project_id=spanner_project,
                            instance_id=spanner_instance,
                            database_id=spanner_database,
                            google_application_credentials=google_application_credentials,
                            emulator_host=spanner_emulator_host,
                            log_dir=log_dir
                        )

    del_time = (datetime.datetime.now() - datetime.timedelta(seconds=time_offset)).isoformat(timespec="milliseconds") + 'Z'

    for table in tables:
        lat = f'AND oxLastAccessTime < "{del_time}" ' if table == 'oxAuthClient' else ''
        nlql = f'SELECT doc_id FROM {table} WHERE exp < "{del_time}" AND del=true {lat}LIMIT {argsp.limit}'
        data = spanner_client.get_dict_data(nlql)
        for entry in data:
            spanner_client.delete_data(table, entry['doc_id'])

else:
    sys.stderr.write(f"Persistence type {persistence_type} is not supported by this script.\n")
