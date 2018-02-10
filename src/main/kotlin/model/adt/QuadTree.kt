package model.adt

import model.geom.Bounded
import model.geom.Box
import model.geom.div

class QuadTree<T : Bounded>(val bounds: Box, val maxObjectsPerLevel: Int = DEFAULT_MAX_OBJECTS_PER_LEVEL, val maxDepth: Int = DEFAULT_MAX_DEPTH, val level: Int = 0) : MutableCollection<T> {

    companion object {
        const val DEFAULT_MAX_OBJECTS_PER_LEVEL = 32
        const val DEFAULT_MAX_DEPTH = 7
    }

    private var childNodes: Array<QuadTree<T>>? = null
    private val objects: MutableList<T> = ArrayList<T>(DEFAULT_MAX_OBJECTS_PER_LEVEL)

    override fun contains(element: T): Boolean {
        return objects.contains(element) || childNodes?.fold(false, { acc, qt -> acc || qt.contains(element) }) ?: false
    }

    override fun containsAll(elements: Collection<T>): Boolean {
        //this may be a simple but suboptimal implementation since the tree has to be traversed elements.size times
        return elements.sumBy { if (contains(it)) 1 else 0 } == elements.size
    }


    override fun isEmpty(): Boolean {
        return objects.isEmpty() && childNodes?.fold(true, { acc, qt -> acc && qt.isEmpty() }) ?: true
    }

    /**
     * Adds a new [element] to the QuadTree
     */
    override fun add(element: T): Boolean {
        //insert object into the correct quadrant.
        childNodes?.forEach {
            if (it.canHold(element))
                return it.add(element)
        }
        //if we reach this, then either we have not yet subdivided, i.e. childNodes == null,
        //or the element's bounds fit into no quadrant. in this case, the element will just
        //be added to this node.
        objects.add(element)

        //when the objects list gets too full, subdivision is triggered
        if (objects.size > maxObjectsPerLevel && childNodes == null && level < maxDepth) {
            subdivide()

            val iter = objects.iterator()
            while (iter.hasNext()) {
                val item = iter.next()
                childNodes!!.forEach {
                    if (it.canHold(item)) {
                        it.add(item)
                        iter.remove()
                        return@forEach
                    }
                }
            }
        }
        return true
    }

    override fun addAll(elements: Collection<T>): Boolean {
        elements.forEach { add(it) }
        return true
    }

    override fun remove(element: T): Boolean {
        return objects.remove(element) || childNodes?.fold(false, { acc, qt -> acc || qt.remove(element) }) ?: false
    }

    override fun removeAll(elements: Collection<T>): Boolean {
        return elements.fold(false, { acc, it -> acc || remove(it) })
    }

    override fun retainAll(elements: Collection<T>): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val size: Int
        get() = objects.size + (childNodes?.sumBy { it.size } ?: 0)

    override fun clear() {
        objects.clear()
        childNodes = null
    }

    override fun iterator(): MutableIterator<T> {

        return object : MutableIterator<T> {

            val objectsIterator = objects.iterator()
            val childIterators: List<MutableIterator<T>>? = childNodes?.map { it.iterator() }

            var objectsIteratorAccessed = false
            var childIteratorsAccessed = arrayOf(false, false, false, false)

            override fun hasNext(): Boolean {
                return objectsIterator.hasNext() || childIterators?.fold(false, { acc, it -> acc || it.hasNext() }) ?: false
            }

            override fun next(): T {
                objectsIteratorAccessed = true
                //as long as the objects list is not fully traversed don't go into sublevels
                if (objectsIterator.hasNext()) {
                    return objectsIterator.next()
                }

                if (childIterators != null) {
                    childIterators.forEachIndexed { index, it ->
                        childIteratorsAccessed[index] = true
                        if (it.hasNext())
                            return it.next()
                    }
                }

                throw NoSuchElementException()
            }

            override fun remove() {
                for (i in 3..0) {
                    if (childIteratorsAccessed[i]) {
                        childIterators?.get(i)?.remove()
                        return
                    }
                }

                if (!objectsIteratorAccessed)
                    throw IllegalStateException()
                objectsIterator.remove()

            }

        }
    }

    /**
     * Retrieves all objects of each quadrant whose bounding boxes overlap with the given [rect]
     *     *
     * @return a list of objects potentially colliding with [rect]
     */
    fun retrieve(rect: Box): List<T> {

        val returnList = arrayListOf<T>()

        //all objects of this node can potentially collide with this rectangle
        returnList.addAll(objects)

        childNodes?.forEach {
            if (rect intersectsWith it.bounds) {
                returnList.addAll(it.retrieve(rect))
            }
        }

        return returnList
    }

    /**
     * Reinserts the element into the tree. This must be called if the bounds of an element have changed.
     */
    fun reinsert(element: T) {
        if(!remove(element))
            throw NoSuchElementException()
        add(element)
    }

    /**
     * Subdivides this QuadTree into quadrants of equal size with the following ordering
     *
     *   II |  I
     *  ----+----
     *  III | IV
     */
    private fun subdivide() {
        val (x, y) = bounds.p1
        val (w, h) = bounds.getSize() / 2
        childNodes = arrayOf(
                QuadTree(Box(x + w, y, w, h), maxObjectsPerLevel, maxDepth, level + 1),
                QuadTree(Box(x, y, w, h), maxObjectsPerLevel, maxDepth, level + 1),
                QuadTree(Box(x, y + h, w, h), maxObjectsPerLevel, maxDepth, level + 1),
                QuadTree(Box(x + w, y + h, w, h), maxObjectsPerLevel, maxDepth, level + 1)
        )
    }

    /**
     * @return true, iff the element's bounding box lies within the bounds of this QuadTree
     */
    private fun canHold(element: Bounded): Boolean {
        val elementBounds = element.getBoundingBox()
        val (x1, y1) = elementBounds.p1
        val (x2, y2) = elementBounds.p2

        return bounds.p1.x <= x1 && bounds.p1.y <= y1 && bounds.p2.x > x2 && bounds.p2.y > y2
    }

    override fun toString(): String {
        var string = "[QuadTree(bounds=$bounds, size=$size, objects={${objects.toString()}}"
        repeat(level, { string = "\t" + string })
        childNodes?.forEach { string += "\n$it" }
        if (childNodes != null) {
            string += "\n"
        }
        string += ")]"
        return string
    }

}