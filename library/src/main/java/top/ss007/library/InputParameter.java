package top.ss007.library;

/**
 * Created by Ben.Wang
 *
 * @author Ben.Wang
 * @modifier
 * @createDate 2020/1/13 14:02
 * @description
 */
public class InputParameter {
    private final String baseUrl;
    private final String relativeUrl;
    private final String loadedFilePath;
    private final boolean isCallbackOnUiThread;

    private InputParameter(Builder builder) {
        this.baseUrl = builder.baseUrl;
        this.relativeUrl = builder.relativeUrl;
        this.loadedFilePath = builder.loadedFilePath;
        this.isCallbackOnUiThread = builder.isCallbackOnUiThread;
    }


    public String getBaseUrl() {
        return baseUrl;
    }

    public String getRelativeUrl() {
        return relativeUrl;
    }

    public String getLoadedFilePath() {
        return loadedFilePath;
    }

    public boolean isCallbackOnUiThread() {
        return isCallbackOnUiThread;
    }

    public static class Builder {
        String baseUrl;
        String relativeUrl;
        String loadedFilePath;
        boolean isCallbackOnUiThread;

        public Builder(String baseUrl, String relativeUrl, String loadedFilePath) {
            this.baseUrl = baseUrl;
            this.relativeUrl = relativeUrl;
            this.loadedFilePath = loadedFilePath;
        }

        /**
         * 设置 onFinish 与onFailed 是否回调在UI线程
         * 注意：onProcess永远回调在UI线程，不受此配置的影响
         *
         * @param callbackOnUiThread
         * @return
         */
        public Builder setCallbackOnUiThread(boolean callbackOnUiThread) {
            isCallbackOnUiThread = callbackOnUiThread;
            return this;
        }


        public InputParameter build() {
            return new InputParameter(this);
        }
    }
}
