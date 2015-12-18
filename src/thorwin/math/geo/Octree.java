/******************************************************************************
 * Copyright (C) 2015 Sebastiaan R. Hogenbirk                                 *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify       *
 * it under the terms of the GNU Lesser General Public License as published by*
 * the Free Software Foundation, either version 3 of the License, or          *
 * (at your option) any later version.                                        *
 *                                                                            *
 * This program is distributed in the hope that it will be useful,            *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of             *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the              *
 * GNU Lesser General Public License for more details.                        *
 *                                                                            *
 * You should have received a copy of the GNU Lesser General Public License   *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package thorwin.math.geo;

import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Math.min;


/**
 * A loose Octree implementation of 3D bounding boxes. Can be used to perform
 * various, high performing selections. The octree is fully immutable.
 */
@SuppressWarnings("unchecked")
public final class Octree<D extends Volume> {

  private static final double DEFAULT_LOOSENESS = 1.5;

  private static final int DEFAULT_MAX_ELEMENTS = 32;

  private static final int OCTO = 8;

  private static final Volume[] EMPTY = new Volume[0];

  /**
   * The octree node's bounding box.
   */
  private final AABB bounds;

  /**
   * The looseness of this octree.
   */
  private final double looseness;

  /**
   * The maximum number of entries this octree uses before splitting leafs.
   */
  private final int max;

  /**
   * The octree node's bounding box.
   */
  private final AABB looseBounds;

  /**
   * The octree node children (or null if a leaf node)
   */
  private final Octree<D>[] children;

  /**
   * The entries stored in this octree node.
   */
  private final D[] entries;


  /**
   * Constructs a new bounding box with the specified boundary.
   *
   * @param bounds bounding box of the root of this octree
   */
  public Octree(AABB bounds) {
    this(bounds, DEFAULT_LOOSENESS, DEFAULT_MAX_ELEMENTS);
  }


  /**
   * Constructs a new bounding box with the specified boundary.
   *
   * @param bounds             bounding box of the root of this octree
   * @param looseness          looseness factor to use
   * @param maxBoundedShape3Ds maximum number of entries a leaf may hold before
   *                           splitting it
   */
  public Octree(AABB bounds, double looseness, int maxBoundedShape3Ds) {
    this(bounds,
         bounds.grow(looseness),
         null,
         (D[]) EMPTY,
         looseness,
         maxBoundedShape3Ds);
  }


  /**
   * Private constructor used by methods of this class.
   *
   * @param bounds   bounds of the node
   * @param children children of the node (may be null to indicate a leaf node)
   * @param entries  entries of the node (not null)
   */
  private Octree(AABB bounds,
                 AABB looseBounds,
                 Octree<D>[] children,
                 D[] entries,
                 double looseness,
                 int max) {
    this.bounds = bounds;
    this.looseBounds = looseBounds;
    this.children = children;
    this.entries = entries;
    this.looseness = looseness;
    this.max = max;
  }


  /**
   * Returns the loose bounds of this octree
   *
   * @return bounding box
   */
  private AABB getLooseBounds() {
    return looseBounds;
  }


  /**
   * Returns the bounding box of this octree
   *
   * @return bounding box
   */
  public AABB getBounds() {
    return bounds;
  }


  /**
   * Selects all objects intersecting the specified bounds.
   *
   * @param bounds bounding box to select
   * @return list of selected objects
   */
  public List<D> select(AABB bounds) {

    List<D> selected = new ArrayList<>();

    select(bounds, selected);

    return selected;
  }


  /**
   * Selects all objects intersecting the specified bounds and stores the result
   * in the specified list.
   *
   * @param bounds bounds
   * @param out    output list
   */
  private void select(AABB bounds, List<D> out) {
    for (Volume element : this.entries) {
      if (element.getBounds().intersects(bounds)) {
        out.add((D) element);
      }
    }

    if (this.children != null) {
      for (Octree<D> child : this.children) {
        if (child.getLooseBounds().intersects(bounds)) {
          child.select(bounds, out);
        }
      }
    }
  }


  /**
   * Removes an entry. Note that this removal will not reduce the depth of the
   * octree.
   *
   * @param data entry to remove
   * @return modified octree
   */
  public Octree<D> delete(D data) {

    AABB bounds = data.getBounds();

    double centerX = bounds.getCenterX();
    double centerY = bounds.getCenterY();
    double centerZ = bounds.getCenterZ();

    for (int i = 0; i < entries.length; i++) {
      Volume entry = this.entries[i];
      if (data.equals(entry)) {
        return deleteEntry(i);
      }
    }


    if (!isLeaf()) {
      Octree[] children = this.children.clone();
      for (int i = 0; i < children.length; i++) {
        Octree<D> child = children[i];
        if (child.getBounds().contains(centerX, centerY, centerZ) &&
            child.getLooseBounds().contains(bounds)) {
          children[i] = child.delete(data);
          return new Octree<>(this.bounds,
                              this.looseBounds,
                              children,
                              this.entries,
                              this.looseness,
                              this.max);
        }
      }
    }

    // apparently, it is not in the octree
    return this;
  }


  /**
   * Removes an entry
   * @param index index of entry
   * @return octree
   */
  private Octree<D> deleteEntry(int index) {

    Volume[] entries = new Volume[this.entries.length -1];

    System.arraycopy(this.entries, 0, entries, 0, index);
    System.arraycopy(this.entries,
                     index + 1,
                     entries,
                     index,
                     entries.length - index);

    return new Octree<>(this.bounds,
                        this.looseBounds,
                        this.children,
                        (D[]) entries,
                        this.looseness,
                        this.max);
  }


  /**
   * Inserts a collection of data. This method is the preferred way of
   * inserting data into an octree.
   *
   * @param data data to insert
   * @return octree
   */
  public Octree<D> insert(Collection<D> data) {
    List<D> list =
        data.stream()
            .collect(Collectors.toList());
    return insertData(list);
  }

  /**
   * Inserts a collection of data. This method is the preferred way of
   * inserting data into an octree.
   *
   * @param data data to insert
   * @return octree
   */
  public Octree<D> insertData(List<D> data) {
    if (isLeaf()) {
      if (entries.length + data.size() > max) {
        return split().insert(data);
      } else {
        return insertEntries(data);
      }
    } else {
      if (entries.length < max) {
        int count = min(max - entries.length, data.size());
        List<D> forOctree = data.subList(0, count);
        List<D> forChildren = data.subList(count, data.size());
        return insertEntries(forOctree).insertChildren(forChildren);
      } else {
        return insertChildren(data);
      }
    }
  }


  /**
   * Inserts the data into the elements of this node.
   * @param data data to insert
   * @return octree
   */
  private Octree<D> insertEntries(List<D> data) {
    Volume[] entries = new Volume[this.entries.length + data.size()];

    for (int i = 0; i < data.size(); i++) {
      entries[i] = data.get(i);
    }

    System.arraycopy(this.entries,
                     0,
                     entries,
                     data.size(),
                     this.entries.length);

    return new Octree<>(bounds,
                        looseBounds,
                        children,
                        (D[]) entries,
                        looseness,
                        max);
  }


  /**
   * Inserts the data into the children of this node, remaining children are
   * inserted into this octree's elements
   * @param data data to insert
   * @return octree
   */
  private Octree<D> insertChildren(List<D> data) {
    Octree<D>[] children = this.children.clone();

    List<D> remaining = new ArrayList<>(data);

    for (int childIndex = 0; childIndex < children.length; childIndex++) {
      Octree<D> child        = children[childIndex];
      List<D>   childEntries = new ArrayList<>(remaining.size());

      for (Iterator<D> i = remaining.iterator(); i.hasNext();) {
        D entry = i.next();

        AABB bounds = entry.getBounds();

        double centerX = bounds.getCenterX();
        double centerY = bounds.getCenterY();
        double centerZ = bounds.getCenterZ();

        if (child.bounds.contains(centerX, centerY, centerZ) &&
            child.looseBounds.contains(bounds)) {
          childEntries.add(entry);
          i.remove();
        }
      }
      if (!childEntries.isEmpty()) {
        children[childIndex] = child.insertData(childEntries);
      }
    }

    return new Octree<>(bounds,
                        looseBounds,
                        children,
                        entries,
                        looseness,
                        max).insertEntries(remaining);
  }


  /**
   * Inserts the entry into the octree. As the octree is immutable, a new octree
   * instance is returned.
   *
   * @param entry entry (not null)
   * @return The new octree, if the entry can actually be inserted to the octree
   * (will return 'this' otherwise).
   */
  public Octree<D> insert(D entry) {

    // no need to insert the entry if its bounds are not contained in this
    // octree
    AABB bounds = entry.getBounds();

    double centerX = bounds.getCenterX();
    double centerY = bounds.getCenterY();
    double centerZ = bounds.getCenterZ();

    if (!(this.bounds.contains(centerX, centerY, centerZ) &&
          this.looseBounds.contains(bounds))) {
      return this;
    }

    if (isLeaf()) {
      if (isFull()) {
        return split().insert(entry);
      }
      else {
        return insertEntry(entry);
      }
    }
    else {
      if (isFull()) {
        return insertChild(entry);
      } else {
        return insertEntry(entry);
      }
    }
  }


  /**
   * Insert an entry
   *
   * @param entry entry to insert
   * @return the octree with the inserted entry
   */
  private Octree<D> insertEntry(D entry) {
    D[] entries = (D[]) new Volume[this.entries.length + 1];

    System.arraycopy(this.entries, 0, entries, 0, this.entries.length);
    entries[this.entries.length] = entry;

    return new Octree<>(this.bounds,
                        this.looseBounds,
                        this.children,
                        entries,
                        looseness,
                        max);
  }


  /**
   * Insert the element in this node, or in any of its children
   *
   * @param element element to insert
   * @return octree with the inserted element
   */
  private Octree<D> insertChild(D element) {

    Octree<D>[] children = this.children.clone();

    for (int i = 0; i < children.length; i++) {
      children[i] = this.children[i].insert(element);
      if (children[i] != this.children[i]) {
        return new Octree<>(this.bounds,
                            this.looseBounds,
                            children,
                            this.entries,
                            looseness,
                            max);
      }
    }

    return insertEntry(element);
  }


  /**
   * Splits a leaf node
   *
   * @return the new octree instance
   */
  @SuppressWarnings("SuspiciousToArrayCall")
  private Octree<D> split() {

    AABB[] boxes = createBoxes();

    Octree<D>[] children = (Octree<D>[]) new Octree[OCTO];

    List<D> elements = new ArrayList<>(Arrays.asList(this.entries));

    for (int i = 0; i < boxes.length; i++) {
      children[i] = new Octree<>(boxes[i]);
      for (Iterator<D> iterator = elements.iterator(); iterator.hasNext(); ) {
        Octree<D> octree = children[i].insert(iterator.next());

        if (octree != children[i]) {
          iterator.remove();
          children[i] = octree;
        }
      }
    }

    return new Octree<>(this.bounds,
                        this.looseBounds,
                        children,
                        (D[]) elements.toArray(new Volume[elements.size()]),
                        looseness,
                        max);
  }


  /**
   * Returns the child boxes in a new array
   *
   * @return child boxes
   */
  private AABB[] createBoxes() {

    double midX = bounds.getMinX() / 2 + bounds.getMaxX() / 2;
    double midY = bounds.getMinY() / 2 + bounds.getMaxY() / 2;
    double midZ = bounds.getMinZ() / 2 + bounds.getMaxZ() / 2;
    double minX = bounds.getMinX();
    double maxX = bounds.getMaxX();
    double minY = bounds.getMinY();
    double maxY = bounds.getMaxY();
    double minZ = bounds.getMinZ();
    double maxZ = bounds.getMaxZ();

    return new AABB[]{new AABB(minX, midX, minY, midY, minZ, midZ),
                      new AABB(minX, midX, minY, midY, midZ, maxZ),
                      new AABB(minX, midX, midY, maxY, minZ, midZ),
                      new AABB(minX, midX, midY, maxY, midZ, maxZ),
                      new AABB(midX, maxX, minY, midY, minZ, midZ),
                      new AABB(midX, maxX, minY, midY, midZ, maxZ),
                      new AABB(midX, maxX, midY, maxY, minZ, midZ),
                      new AABB(midX, maxX, midY, maxY, midZ, maxZ)};
  }


  /**
   * Determine if this node is full.
   *
   * @return true if the node is full (meaning it should be split when adding
   * more entries).
   */
  private boolean isFull() {
    return this.entries.length >= max;
  }


  @Override
  public String toString() {
    if (isLeaf()) {
      return "LEAF box=" + bounds + " entries=" + entries.length;
    }
    else {
      return "NODE box=" + bounds + " entries=" + entries.length +
             " depth=" + depth();
    }
  }


  /**
   * Returns the depth the octree.
   *
   * @return depth
   */
  public int depth() {
    if (isLeaf()) {
      return 0;
    }
    else {
      int depth = 0;
      for (Octree<D> child : this.children) {
        depth = Math.max(depth, child.depth());
      }
      return depth + 1;
    }
  }


  /**
   * Returns the maximum entries stored in a single node for this tree. The
   * maximum entries in a node is a performance indicator for the octree.
   *
   * @return maximum entries in node
   */
  public int maxEntries() {
    if (isLeaf()) {
      return this.entries.length;
    }
    else {
      int max = this.entries.length;
      for (Octree<D> child : this.children) {
        max = Math.max(max, child.maxEntries());
      }
      return max;
    }
  }


  /**
   * Returns the total of entries stored at the specified level in the tree.
   * This is a performance indicator for the octree.
   *
   * @param level depth level to query
   * @return number of entries at level
   */
  public int totalAt(int level) {
    if (level < 0) {
      return 0;
    }
    if (level == 0) {
      int max = 0;
      if (!isLeaf()) {
        for (Octree<D> child : this.children) {
          max += child.entries.length;
        }
      }
      else {
        max = entries.length;
      }
      return max;

    }
    else {
      int max = 0;
      if (!isLeaf()) {
        for (Octree<D> child : this.children) {
          max += child.totalAt(level - 1);
        }
      }
      return max;
    }
  }


  /**
   * Returns the number of entries in this octree. This method calculates the
   * size by iterating recursively over the entire octree. This means that
   * this method is only intended for debugging purposes.
   * @return number of entries in this octree.
   */
  public int size() {
    if (isLeaf()) {
      return entries.length;
    } else {
      return Arrays.stream(children)
                   .mapToInt(Octree::size).sum() +
             entries.length;
    }
  }


  /**
   * Determines if this is a leaf node
   *
   * @return true if this octree is a leaf
   */
  private boolean isLeaf() {
    return this.children == null;
  }
}
