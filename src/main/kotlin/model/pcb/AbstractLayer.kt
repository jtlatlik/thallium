package model.pcb

/**
 * An AbstractLayer is a physical layer or an abstract (set) of layers that a primitive can be assigned to.
 *
 * For instance, if a primitive is assigned to the master layer [ALL_LAYERS] then it will be drawn on each physical
 * layer (e.g. used for most vias)
 *
 */
sealed class AbstractLayer {


    /**
     * An abstract layer representing all physical layers.
     */
    object ALL_LAYERS : AbstractLayer()

    /**
     * An abstract layer representing all inner signal layers.
     */
    object MID_LAYERS : AbstractLayer()

    /**
     * An abstract layer representing all signal layers, i.e Top, Bottom, and Mid(n).
     */
    object SIGNAL_LAYERS : AbstractLayer()

    /**
     * An abstract layer representing all physical layers
     */
    object PHYSICAL_LAYERS : AbstractLayer()

    /**
     * An abstract layer representing all auxiliary layers
     */
    object AUXILIARY_LAYERS : AbstractLayer()

    /**
     * An abstract layer class representing all possible forms of copper layers
     */
    sealed class CopperLayer : AbstractLayer() {

        /**
         *  An abstract layer representing the top-most signal layer in a stackup.
         */
        object Top : CopperLayer()

        /**
         *  An abstract layer representing the bottom-most signal layer in a stackup.
         */
        object Bottom : CopperLayer()

        /**
         *  An abstract layer representing an inner signal layer.
         */
        data class Mid(val index: Int) : CopperLayer()

        /**
         *  An abstract layer representing a copper plane layer.
         */
        data class Plane(val index: Int) : CopperLayer()

        /**
         * An abstract layer representing a range of copper layers. Can be used for blind vias and buried vias.
         */
        data class Range(val start: CopperLayer, val stop: CopperLayer) : AbstractLayer()

    }

    /**
     * An abstract layer class representing all possible forms of auxiliary layers
     */
    sealed class AuxiliaryLayer : AbstractLayer() {

        /**
         * An abstract layer representing the layer which is used to define the board shape of the PCB.
         */
        object BoardShape : AuxiliaryLayer()

        /**
         * An abstract layer representing a keepout layer.
         */
        object Keepout : AuxiliaryLayer()

        object TopPaste : AuxiliaryLayer()
        object BottomPaste : AuxiliaryLayer()

        data class Aux(val index: Int) : AuxiliaryLayer()
    }



    /**
     * An abstract layer class representing all possible forms of auxiliary layers
     */
    sealed class DielectricLayer : AbstractLayer() {

        object TopSilk : DielectricLayer()
        object BottomSilk : DielectricLayer()

        object TopSolderMask : DielectricLayer()
        object BottomSolderMask : DielectricLayer()
    }

    /**
     * An abstract layer representing one ore more, but not (necessarily) all layers
     * Currently only used for pad containers.
     */
    object MULTIPLE_LAYERS : AbstractLayer()


}