package solo_game.dataStructures;

import java.util.Iterator;

import org.joml.Vector3f;
import solo_game.ColoredBox;
import solo_game.LevelEditorScene;

public class MultiLinkedList implements Iterable<Node<Data>> {

    private Node<Data> head;

    public static int xMargin = 2;
    public static int yMargin = 2;

    public MultiLinkedList(int n) {
        Node<Data> lastChild = null;
        Node<Data> lastNext = null;

        for (int x = 0; x < n; x++) {
            for (int y = n - 1; y >= 0; y--) {
                Data d = new Data(x + 1, y + 1,
                        new ColoredBox(new Vector3f((x - n / 2) * xMargin, (y - n / 2) * yMargin, 0)));
                Node<Data> toAdd = new Node<>(d);
                if (head == null) {
                    head = toAdd;
                    lastChild = head;
                    lastNext = head;
                } else if (y == n - 1) {
                    lastChild.child = toAdd;
                    lastNext = toAdd;
                    lastChild = toAdd;
                } else {
                    lastNext.next = toAdd;
                    lastNext = toAdd;
                }
            }
        }

        deleteAfter((n / 2) + 1, n / 2 + 2);
        deleteAfter((n / 2) + 1, n / 2 + 2);
        deleteAfter((n / 2), n / 2 + 2);
        deleteAfter((n / 2), n / 2 + 2);
    }

    private final boolean deleteAfter(int row, int col) {
        Node<Data> searched;
        for (Node<Data> node : this) {
            if (!node.data.equal(row, col)) {
                continue;
            }

            searched = node;
            if (searched.next == null) {
                return false;
            }

            Node<Data> next = searched.next;
            if (next.next != null) {
                searched.next = next.next;
            } else {
                searched.next = null;
            }
            return true;
        }
        return false;
    }

    private final void addFirst(Node<Data> nodeToAdd) {

        if (head.child != null) {
            nodeToAdd.child = head.child;
        }
        nodeToAdd.next = head;
        head = nodeToAdd;
    }

    public final boolean add(Node<Data> nodeToAdd) {

        if (head == null) {
            head = nodeToAdd;
            return true;
        }

        // find previous
        Node<Data> prev = head;
        while (prev != null && nodeToAdd.data.x != prev.data.x) {
            prev = prev.child;
        }

        // there is no previous
        if (prev == null) {
            // left
            if (head.data.x > nodeToAdd.data.x) {
                nodeToAdd.child = head;
                head = nodeToAdd;
                return true;
            }

            if (head.data.x == nodeToAdd.data.x) {
                System.out.println("hataaa!!!");
            }

            Node<Data> neigbour = head;
            while (neigbour.child != null && neigbour.child.data.x < neigbour.data.x) {
                neigbour = neigbour.child;
            }

            if (neigbour.child != null) {
                // middle
                nodeToAdd.child = neigbour.child;
                neigbour.child = nodeToAdd;
            } else {
                // right
                neigbour.child = nodeToAdd;

            }

            return true;
        }

        // has previous and top of previous
        if (nodeToAdd.data.y > prev.data.y) {

            if (prev.equals(head)) {
                addFirst(nodeToAdd);
                return true;
            }

            Node<Data> parentOfPrev = null;
            Node<Data> prevOfPrev = null;
            for (Node<Data> node : this) {
                if (node.child != null && node.child.equals(prev)) {
                    parentOfPrev = node;
                    break;
                }
                if (node.next != null && node.next.equals(prev)) {
                    prevOfPrev = node;
                    break;
                }
            }

            if (parentOfPrev != null) {
                parentOfPrev.child = nodeToAdd;
                nodeToAdd.next = prev;
            } else {
                prevOfPrev.next = nodeToAdd;
                nodeToAdd.next = prev;
            }

            if (prev.child != null) {
                nodeToAdd.child = prev.child;
            }

            return true;
        }
        // has previous and bottom of previous
        while (prev.next != null && prev.next.data.y > nodeToAdd.data.y) {
            prev = prev.next;
        }

        if (prev.next != null) {
            nodeToAdd.next = prev.next;
            prev.next = nodeToAdd;
        } else {
            prev.next = nodeToAdd;
        }

        return true;
    }

    private final void removeHead() {
        Node<Data> headDelete = head;

        if (head.next != null) {
            if (head.child != null) {
                head.next.child = head.child;
            }

            head = head.next;
        } else {
            if (head.child != null) {
                head = head.child;
            } else {
                head = null;
            }

        }

        headDelete.next = null;
        headDelete.child = null;

    }

    public final boolean delete(Node<Data> nodeToDelete) {
        Node<Data> prev = null;
        Node<Data> parent = null;

        if (nodeToDelete.equals(head)) {
            removeHead();
            return true;
        }

        for (Node<Data> node : this) {
            if (node.next != null && node.next.equals(nodeToDelete)) {
                prev = node;
                break;
            }
            if (node.child != null && node.child.equals(nodeToDelete)) {
                parent = node;
                break;
            }
        }

        if (prev == null && parent == null) {
            return false;
        }

        // has parent
        if (prev != null) {

            // middle
            if (nodeToDelete.next != null) {
                prev.next = nodeToDelete.next;
            } // end
            else {
                prev.next = null;
            }

        } else if (parent != null) {
            if (nodeToDelete.next != null) {
                parent.child = nodeToDelete.next;

                if (nodeToDelete.child != null) {
                    parent.child.child = nodeToDelete.child;
                }

            } else {
                if (nodeToDelete.child != null) {
                    parent.child = nodeToDelete.child;

                } else {
                    parent.child = null;
                }
            }

        } else {
            System.out.println("hataaaa!!");
        }
        nodeToDelete.next = null;
        nodeToDelete.child = null;
        return true;

    }

    public Move right(Node<Data> base) {
        Node<Data> next = null;
        for (Node<Data> node : this) {
            if (node.data.equal(base.data.x + 1, base.data.y)) {
                next = node;
                break;
            }
        }
        // if there is no next return null
        if (next == null) {
            return null;
        }

        // if next has other next return null
        for (Node<Data> node : this) {
            if (node.data.equal(next.data.x + 1, next.data.y)) {
                return null;
            }
        }

        // if next is last element return null
        if (next.data.x == LevelEditorScene.N) {
            return null;
        }

        return new Move(next, base, next.data.box.getPos().add(2, 0, 0), this);
    }

    public Move left(Node<Data> base) {
        Node<Data> next = null;
        for (Node<Data> node : this) {
            if (node.data.equal(base.data.x - 1, base.data.y)) {
                next = node;
                break;
            }
        }
        // if there is no next return null
        if (next == null) {
            return null;
        }

        // if next has other next return null
        for (Node<Data> node : this) {
            if (node.data.equal(next.data.x - 1, next.data.y)) {
                return null;
            }
        }

        // if next is last element return null
        if (next.data.x == 1) {
            return null;
        }
        return new Move(next, base, next.data.box.getPos().add(-2, 0, 0), this);
    }

    public Move up(Node<Data> base) {
        Node<Data> next = null;
        for (Node<Data> node : this) {
            if (node.data.equal(base.data.x, base.data.y + 1)) {
                next = node;
                break;
            }
        }
        // if there is no next return null
        if (next == null) {
            return null;
        }

        // if next has other next return null
        for (Node<Data> node : this) {
            if (node.data.equal(next.data.x, next.data.y + 1)) {
                return null;
            }
        }

        // if next is last element return null
        if (next.data.y == LevelEditorScene.N) {
            return null;
        }

        return new Move(next, base, next.data.box.getPos().add(0, 2, 0), this);
    }

    public Move down(Node<Data> base) {
        Node<Data> next = null;
        for (Node<Data> node : this) {
            if (node.data.equal(base.data.x, base.data.y - 1)) {
                next = node;
                break;
            }
        }
        // if there is no next return null
        if (next == null) {
            return null;
        }

        // if next has other next return null
        for (Node<Data> node : this) {
            if (node.data.equal(next.data.x, next.data.y - 1)) {
                return null;
            }
        }

        // if next is last element return null
        if (next.data.y == 1) {
            return null;
        }

        return new Move(next, base, next.data.box.getPos().add(0, -2, 0), this);
    }

    @Override
    public Iterator<Node<Data>> iterator() {

        return new Iterator<>() {
            Node<Data> lastNext = null;
            Node<Data> lastChild = null;
            boolean started = false;

            @Override
            public boolean hasNext() {
                if (head == null) {
                    return false;
                }
                if (!started) {
                    started = true;
                    return true;
                }
                return lastNext.next != null || lastChild.child != null;
            }

            @Override
            public Node<Data> next() {
                if (lastNext == null && head != null) {
                    lastNext = lastChild = head;
                    return head;
                }
                if (lastNext.next == null) {

                    // lastChild.child is valid because of hasNext method
                    Node<Data> n = lastChild.child;
                    lastNext = lastChild = n;
                    return n;
                }

                Node<Data> n = lastNext.next;
                lastNext = n;
                return n;
            }
        };
    }
}
