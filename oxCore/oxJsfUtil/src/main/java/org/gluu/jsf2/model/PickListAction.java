package org.gluu.jsf2.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.gluu.util.StringHelper;

@Dependent
@Named("pickListAction")
public class PickListAction<T> {
	
    @Inject
    private ExternalContext externalContext;

    public void moveAll(List<T> srcList, List<T> dstList) {
        for (T attr : srcList) {
        	dstList.add(attr);
        }
        srcList.clear();
    }

    public List<T> retainUnique(List<T> srcList, List<T> dstList) {
    	if ((srcList == null) || srcList.isEmpty()) {
    		return new ArrayList<T>();
    	}

    	List<T> resultList = new ArrayList<T>(srcList);
    	if ((dstList == null) || dstList.isEmpty()) {
    		return resultList;
    	}
    	
    	for (Iterator<T> it = resultList.iterator(); it.hasNext();) {
			T t = (T) it.next();
			if (dstList.contains(t)) {
				it.remove();
			}
		}

    	return resultList;
    }

    public void move(List<T> srcList, List<T> dstList) {
    	String[] indexArray = externalContext.getRequestParameterMap().get("indexes").split(",");
    	Arrays.sort(indexArray);

    	for (String index: indexArray) {
        	int pos = StringHelper.toInteger(index, -1);

        	if (pos < srcList.size()) {
                T item = srcList.get(pos);
                dstList.add(item);
            }
    	}

    	Arrays.sort(indexArray, Collections.reverseOrder());
    	for (String index: indexArray) {
        	int pos = StringHelper.toInteger(index, -1);

        	if (pos < srcList.size()) {
                srcList.remove(pos);
            }
    	}
    }
}
