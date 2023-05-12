

public class BTree {
    private int t;
    BTreeNode root;

    public BTree(int t) {
        this.t = t;
        root = null;
    }

    public void insert(FunkoPop item) {
        if (root == null) {
            root = new BTreeNode(t, true);
            root.keys[0] = item;
            root.numKeys = 1;
        } else {
            if (root.numKeys == 2*t-1) {
                BTreeNode newRoot = new BTreeNode(t, false);
                newRoot.children[0] = root;
                newRoot.splitChild(0, root);
                int i = 0;
                if (newRoot.keys[0].compareTo(item) < 0) {
                    i++;
                }
                newRoot.children[i].insertNonFull(item);
                root = newRoot;
            } else {
                root.insertNonFull(item);
            }
        }
    }
    public void delete(FunkoPop item) {
        if (root == null) {
            return;
        }
        root.delete(item);
        if (root.numKeys == 0) {
            if (root.leaf) {
                root = null;
            } else {
                root = root.children[0];
            }
        }
    }


    public void print() {
        if (root != null) {
            root.print();
        }
    }
    
    public boolean search(FunkoPop key) {
        if (root != null) {
            return root.search(key);
        }
        return false;
    }

    public FunkoPop retrieve(FunkoPop key) {
        if (root != null) {
            return root.retrieve(key);
        }
        return null;
    }

    private class BTreeNode {
        private int t;
        private FunkoPop[] keys;
        private BTreeNode[] children;
        private int numKeys;
        private boolean leaf;

        public BTreeNode(int t, boolean leaf) {
            this.t = t;
            this.leaf = leaf;
            keys = new FunkoPop[2*t-1];
            children = new BTreeNode[2*t];
            numKeys = 0;
        }

        public void delete(FunkoPop item) {
            int i = 0;
            while (i < numKeys && keys[i].compareTo(item) < 0) {
                i++;
            }
            if (i < numKeys && keys[i].compareTo(item) == 0) {
                if (leaf) {
                    for (int j = i; j < numKeys-1; j++) {
                        keys[j] = keys[j+1];
                    }
                    numKeys--;
                } else {
                    FunkoPop pred = getPredecessor(i);
                    keys[i] = pred;
                    children[i].delete(pred);
                }
            } else {
                if (leaf) {
                    return;
                }
                boolean flag = (i == numKeys);
                if (children[i].numKeys < t) {
                    fill(i);
                }
                if (flag && i > numKeys) {
                    children[i-1].delete(item);
                } else {
                    children[i].delete(item);
                }
            }
        }
        
        private void fill(int i) {
            if (i > 0 && children[i-1].numKeys >= t) {
                borrowFromLeft(i);
            } else if (i < numKeys && children[i+1].numKeys >= t) {
                borrowFromRight(i);
            } else {
                merge(i);
            }
        }
        
        private void borrowFromLeft(int i) {
            BTreeNode child = children[i];
            BTreeNode leftSibling = children[i-1];
            for (int j = child.numKeys-1; j >= 0; j--) {
                child.keys[j+1] = child.keys[j];
            }
            if (!child.leaf) {
                for (int j = child.numKeys; j >= 0; j--) {
                    child.children[j+1] = child.children[j];
                }
            }
            child.keys[0] = keys[i-1];
            if (!child.leaf) {
                child.children[0] = leftSibling.children[leftSibling.numKeys];
            }
            keys[i-1] = leftSibling.keys[leftSibling.numKeys-1];
            child.numKeys++;
            leftSibling.numKeys--;
        }
        
        private void borrowFromRight(int i) {
            BTreeNode child = children[i];
            BTreeNode rightSibling = children[i+1];
            child.keys[child.numKeys] = keys[i];
            if (!child.leaf) {
                child.children[child.numKeys+1] = rightSibling.children[0];
            }
            keys[i] = rightSibling.keys[0];
            for (int j = 1; j < rightSibling.numKeys; j++) {
                rightSibling.keys[j-1] = rightSibling.keys[j];
            }
            if (!rightSibling.leaf) {
                for (int j = 1; j <= rightSibling.numKeys; j++) {
                    rightSibling.children[j-1] = rightSibling.children[j];
                }
            }
            child.numKeys++;
            rightSibling.numKeys--;
        }

        private void merge(int i) {
            BTreeNode child = children[i];
            BTreeNode sibling = children[i+1];
            child.keys[t-1] = keys[i];
            for (int j = 0; j < sibling.numKeys; j++) {
                child.keys[j+t] = sibling.keys[j];
            }
            if (!child.leaf) {
                for (int j = 0; j <= sibling.numKeys; j++) {
                    child.children[j+t] = sibling.children[j];
                }
            }
            for (int j = i+1; j < numKeys; j++) {
                keys[j-1] = keys[j];
            }
            for (int j = i+2; j <= numKeys; j++) {
                children[j-1] = children[j];
            }
            child.numKeys += sibling.numKeys+1;
            numKeys--;
        }
        
        
        
        public boolean search(FunkoPop key) {
            int i = 0;
            while (i < numKeys && key.compareTo(keys[i]) > 0) {
                i++;
            }

            if (i < numKeys && key.compareTo(keys[i]) == 0) {
                return true;
            } else if (leaf) {
                return false;
            } else {
                return children[i].search(key);
            }
        }

        public FunkoPop retrieve(FunkoPop key) {
            int i = 0;
            while (i < numKeys && key.compareTo(keys[i]) > 0) {
                i++;
            }

            if (i < numKeys && key.compareTo(keys[i]) == 0) {
                return keys[i];
            } else if (leaf) {
                return null;
            } else {
                return children[i].retrieve(key);
            }
        }
       
      
        private FunkoPop getPredecessor(int i) {
            BTreeNode curr = children[i];
            while (!curr.leaf) {
                curr = curr.children[curr.numKeys];
            }
            return curr.keys[curr.numKeys-1];
        }

		public void insertNonFull(FunkoPop item) {
            int i = numKeys-1;
            if (leaf) {
                while (i >= 0 && keys[i].compareTo(item) > 0) {
                    keys[i+1] = keys[i];
                    i--;
                }
                keys[i+1] = item;
                numKeys++;
            } else {
                while (i >= 0 && keys[i].compareTo(item) > 0) {
                    i--;
                }
                if (children[i+1].numKeys == 2*t-1) {
                    splitChild(i+1, children[i+1]);
                    if (keys[i+1].compareTo(item) < 0) {
                        i++;
                    }
                }
                children[i+1].insertNonFull(item);
            }
        }

        public void splitChild(int i, BTreeNode y) {
            BTreeNode z = new BTreeNode(t, y.leaf);
            z.numKeys = t-1;
            for (int j = 0; j < t-1; j++) {
                z.keys[j] = y.keys[j+t];
            }
            if (!y.leaf) {
                for (int j = 0; j < t; j++) {
                    z.children[j] = y.children[j+t];
                }
            }
            y.numKeys = t-1;
            for (int j = numKeys; j >= i+1; j--) {
                children[j+1] = children[j];
            }
            children[i+1] = z;
            for (int j = numKeys-1; j >= i; j--) {
                keys[j+1] = keys[j];
            }
            keys[i] = y.keys[t-1];
            numKeys++;
        }

        public void print() {
            for (int i = 0; i < numKeys; i++) {
                if (!leaf) {
                    children[i].print();
                }
                System.out.println(keys[i].toString());
            }
            if (!leaf) {
                children[numKeys].print();
            }
        }
    }
}