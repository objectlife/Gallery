package com.weshare.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by mrsimple on 16/4/2018.
 */

public class Category {
    public String id ;
    public String name ;
    public List<CateTag> tagsList = new LinkedList<>();

    @Override
    public String toString() {
        return "Category{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }

    public void addTag(CateTag tag) {
        if ( tag != null ) {
            tagsList.add(tag) ;
        }
    }
}
