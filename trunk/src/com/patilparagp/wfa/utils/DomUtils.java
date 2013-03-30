package com.patilparagp.wfa.utils;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DomUtils {
    public static Node getNodeByName(NodeList nodeList, String nodeName) {
        for (int index = 0; index < nodeList.getLength(); index++) {
            Node node = nodeList.item(index);
            if (nodeName.equals(node.getNodeName())) {
                return node;
            }
        }
        return null;
    }
}
