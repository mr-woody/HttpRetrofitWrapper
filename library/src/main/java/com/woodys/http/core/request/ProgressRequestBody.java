package com.woodys.http.core.request;

import com.woodys.http.utils.Preconditions;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * Decorates an OkHttp request body to count the number of bytes written when writing it. Can
 * decorate any request body, but is most useful for tracking the upload inProgress of large
 * multipart requests.
 *
 * @author Created by woodys on 2020/6/16.
 * @email yuetao.315@qq.com
 */
public abstract class ProgressRequestBody extends RequestBody {
    private final RequestBody delegate;

    public ProgressRequestBody(RequestBody delegate) {
        Preconditions.checkNotNull(delegate, "delegate==null");
        this.delegate = delegate;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return delegate.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        BufferedSink bufferedSink = Okio.buffer(sink(sink));
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected abstract void onUpload(long progress, long contentLength, boolean done);

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            private long bytesWritten = 0L;
            private long contentLength = -1L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                bytesWritten += byteCount;
                if (contentLength == -1) {
                    //避免多次调用
                    contentLength = contentLength();
                }
                onUpload(bytesWritten, contentLength, bytesWritten == contentLength);
            }
        };
    }
}