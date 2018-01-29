package model

enum class LayerType(val text: String) {
    SIGNAL("Signal"),
    DIELECTRIC("Dielectric"),
    SOLDER_MASK("Solder Mask"),
    PASTE_MASK("Paste Mask"),
    PLANE("Internal Plane"),
    SILK("Silk Screen");

    override fun toString(): String {
        return text
    }
}