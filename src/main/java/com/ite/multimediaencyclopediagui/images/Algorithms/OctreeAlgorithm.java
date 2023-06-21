package com.ite.multimediaencyclopediagui.images.Algorithms;

import com.ite.multimediaencyclopediagui.images.Pixel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OctreeAlgorithm {
    public static Pixel[] GetQuantizedPixels(Pixel[] imagePixels, int nColors) {
        Pixel[] ans = new Pixel[imagePixels.length];
        Map<Pixel, Pixel> colorMap = new HashMap<>();
        int depth = 3;
        if (nColors <= 8) {
//            nColors = 8;
            depth = 1;
        } else if (nColors <= 64)
            depth = 2;
        Octree node = new Octree();
        node.populate(depth);
        for (Pixel color : imagePixels) {
            String R = intToBinaryString(color.RGB[0]);
            String G = intToBinaryString(color.RGB[1]);
            String B = intToBinaryString(color.RGB[2]);
            Octree tempNode = node;
            for (int i = 0; i < depth; i++) {
                String firstDigit1 = R.substring(i, i + 1);
                String firstDigit2 = G.substring(i, i + 1);
                String firstDigit3 = B.substring(i, i + 1);
                String num = firstDigit1 + firstDigit2 + firstDigit3;
                int index = Integer.parseUnsignedInt(num, 2);
                tempNode = tempNode.children.get(index);
                if(depth-i==1){
                    tempNode.addColor(color);
                    break;
                }
            }
        }
        node.reduce(node, nColors);
        node.mapColors(colorMap);
        for (int i = 0; i < ans.length; i++) {
            ans[i] = new Pixel();
            ans[i].RGB = colorMap.get(imagePixels[i]).RGB;
            ans[i].index = i;
        }
        return ans;
    }

    public static String intToBinaryString(int n) {
        String binary = Integer.toBinaryString(n);
        int padding = 8 - binary.length();
        if (padding < 0) {
            throw new IllegalArgumentException("Input integer is larger than 8 bits");
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < padding; i++) {
            sb.append('0');
        }
        sb.append(binary);
        return sb.toString();
    }
}

class Octree {
    ArrayList<Pixel> colors;
    ArrayList<Octree> children;
    ArrayList<Octree> leafs;

    public Octree() {
        colors = new ArrayList<>();
        children = new ArrayList<>();
        leafs = new ArrayList<>();
    }

    public static int[] getGroupCenter(ArrayList<Pixel> group) {
        int[] ans = new int[3];
        for (Pixel pixel : group) {
            ans[0] += pixel.RGB[0];
            ans[1] += pixel.RGB[1];
            ans[2] += pixel.RGB[2];
        }
        ans[0] /= group.size();
        ans[1] /= group.size();
        ans[2] /= group.size();
        return ans;
    }

    public void addChild(Octree o) {
        children.add(o);
    }

    public void addColor(Pixel color) {
        colors.add(color);
    }

    public void populate(int depth) {
        if (depth == 0)
            return;
        for (int j = 0; j < 8; j++) {
            addChild(new Octree());
            children.get(j).populate(depth - 1);
        }
        if (depth == 1) {
            leafs.addAll(children);
        }
        for (Octree child : children) {
            leafs.addAll(child.leafs);
        }
    }

    public void reduce(Octree octree, int maxLeafs) {
        if (octree.leafs.size() == 0) {
            return;
        }
        if (octree.leafs.size() <= maxLeafs) {
            return;
        }
        for (Octree child : octree.children) {
            reduce(child, maxLeafs);
        }

        ArrayList<Pixel> combinedColors = new ArrayList<>();
        for (Octree child : octree.children) {
            combinedColors.addAll(child.colors);
        }
        octree.colors = combinedColors;

        Octree leaf = new Octree();
        leaf.colors = octree.colors;
        octree.children.clear();
        octree.leafs.clear();
        octree.leafs.add(leaf);
    }

    public void mapColors(Map colorMap) {
        for (Octree leaf : leafs) {
            if (leaf.colors.size() != 0) {
                Pixel p = new Pixel();
                p.RGB = getGroupCenter(leaf.colors);
                for (Pixel color : leaf.colors) {
                    colorMap.put(color, p);
                }
            }
        }
    }

}

