package com.weshare.domain;

import java.util.List;

/**
 * Created by mrsimple on 16/4/2018.
 */

public class Category {
    public String id ;
    public String name ;
    public List<CateTag> tags ;

    @Override
    public String toString() {
        return "Category{" + "id='" + id + '\'' + ", name='" + name + '\'' + '}';
    }
}
