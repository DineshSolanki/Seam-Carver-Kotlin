package seamCarver

enum class FileTypes(val fileType: String) {
    Image("image/*"),
    Video("video/*"),
    Audio("audio/*");

    override fun toString(): String {
        return fileType
    }

}