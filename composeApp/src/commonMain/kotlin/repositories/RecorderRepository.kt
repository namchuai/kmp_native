package repositories

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class RecorderRepository {
    /**
     * Start recording audio
     */
    fun startRecording()

    /**
     * Stop the current recording and save the file
     *
     * @return Path to the saved audio file, or null if an error occurred
     */
    fun stopRecording(): String?

    /**
     * Release all resources used by the recorder
     * Should be called when the recorder is no longer needed
     */
    fun release()
}