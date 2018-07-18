package com.example.eros.common.utils;

import com.example.eros.common.domain.Tree;

import java.util.ArrayList;
import java.util.List;

/**
 * 对菜单栏(树目录)进行排序
 */
public class BuildTreeUtils {

    /**
     * 将菜单的父子关系在这里进行相互连接
     * @param treeList 原始未连接的树参数集
     * @param idParam 默认顶级菜单
     * @param <T> 转换过的树参数集
     * @return
     */
    public static <T> List<Tree<T>> buildList(List<Tree<T>> treeList,String idParam) {

        if(treeList == null){
            return null;
        }

        //将所有菜单进行父子判断
        List<Tree<T>> nodes = new ArrayList<Tree<T>>();
        for(Tree<T> children : treeList){
            String parentId = children.getParentId();
            if(parentId == null || parentId.equals(idParam)){
                nodes.add(children);
            }

            for(Tree<T> parent : treeList){
                String id = parent.getId();
                if(id != null && id.equals(parentId)){
                    children.setHasParent(true);
                    parent.setHasChildren(true);
                    parent.getChildren().add(children);
                    continue;
                }
            }
        }

//        Tree<T> root = new Tree<T>();
//        //如果顶级菜单只有一条，那么根就是他，否则重新进行创建设置root
//        if(nodes.size() == 1){
//            root = nodes.get(0);
//        } else {
//            root.setId("-1");
//            root.setParentId("");
//            root.setHasParent(false);
//            root.setHasChildren(true);
//            root.setChildren(nodes);
//            root.setChecked(true);
//            root.setText("顶级节点");
//            Map<String, Object> state = new HashMap<>(16);
//            state.put("opened", true);
//            root.setState(state);
//        }
        return nodes;
    }

}
