package model.geom

/**
 * An interface for objects which are bounded in the 2D plane
 */
interface Bounded {

    /**
     * @return the 2D-axis-aligned bounding box of the implementing object
     */
    fun getBoundingBox(): Box

}