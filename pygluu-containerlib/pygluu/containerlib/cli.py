import click

from pygluu.containerlib import get_manager
from pygluu.containerlib.utils import decode_text
from pygluu.containerlib.utils import encode_text


@click.command(help="Encode string")
@click.argument("text")
def encode(text):
    manager = get_manager()
    enc_text = encode_text(text, manager.secret.get("encoded_salt")).decode()
    click.echo(enc_text)


@click.command(help="Decode string")
@click.argument("text")
def decode(text):
    manager = get_manager()
    dec_text = decode_text(text, manager.secret.get("encoded_salt")).decode()
    click.echo(dec_text)


@click.group(
    context_settings={"help_option_names": ["-h", "--help"]}
)
def cli():
    ...


cli.add_command(encode)
cli.add_command(decode)
