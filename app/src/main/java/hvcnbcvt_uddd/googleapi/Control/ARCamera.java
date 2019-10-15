package hvcnbcvt_uddd.googleapi.Control;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.opengl.Matrix;
import android.os.Build;
import android.util.Size;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.io.IOException;
import java.util.List;

@SuppressWarnings("deprecation")
@TargetApi(Build.VERSION_CODES.KITKAT)
public class ARCamera extends ViewGroup implements SurfaceHolder.Callback {

    // Cung cấp mặt phẳng có thể vẽ liên tục
    SurfaceView surfaceView;
    // Thực hiện các thao tác thay đổi trên màn hình
    SurfaceHolder surfaceHolder;
    Camera.Size previewSize;
    List<Camera.Size> supportedPreviewSizes;
    Camera camera;
    Camera.Parameters parameters;

    Activity activity;

    float[] projectionMatrix = new float[16];

    int cameraWidth;
    int cameraHeight;
    private final static float Z_NEAR = 0.5f;
    private final static float Z_FAR = 2000;

    //vẽ ảnh từ camera lên
    public ARCamera(Context context, SurfaceView surfaceView) {
        super(context);

        this.surfaceView = surfaceView;
        this.activity = (Activity) context;
        surfaceHolder = this.surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
        if (this.camera != null) {
            supportedPreviewSizes = this.camera.getParameters().getSupportedPreviewSizes();
            requestLayout();
            Camera.Parameters params = this.camera.getParameters();

            List<String> focusModes = params.getSupportedFocusModes();
            if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
                this.camera.setParameters(params);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (supportedPreviewSizes != null) {
            previewSize = getOptimalPreviewSize(supportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = right - left;
            final int height = bottom - top;

            int previewWidth = width;
            int previewHeight = height;
            if (previewSize != null) {
                previewWidth = previewSize.width;
                previewHeight = previewSize.height;
            }

            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    // điều chỉnh cho camera orientation khớp với orientation
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (camera != null) {

                parameters = camera.getParameters();

                int orientation = getCameraOrientation();

                camera.setDisplayOrientation(orientation);
                camera.getParameters().setRotation(orientation);

                camera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
        }
    }

    private int getCameraOrientation() {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);

        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();

        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int orientation;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = (info.orientation + degrees) % 360;
            orientation = (360 - orientation) % 360;
        } else {
            orientation = (info.orientation - degrees + 360) % 360;
        }

        return orientation;
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        if (camera != null) {
            camera.setPreviewCallback(null);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }


    private Camera.Size getOptimalPreviewSize(List<Camera.Size> sizes, int width, int height) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) width / height;
        if (sizes == null) return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = height;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) {
                continue;
            }

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        if (optimalSize == null) {
            optimalSize = sizes.get(0);
        }

        return optimalSize;
    }

    // preview size cho ảnh hiển thị và set vào camera parameter
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (camera != null) {
            this.cameraWidth = width;
            this.cameraHeight = height;

            Camera.Parameters params = camera.getParameters();
            List<Camera.Size> sizes = camera.getParameters().getSupportedPreviewSizes();
//            params.setPreviewSize(sizes.get(0).width, sizes.get(0).height);
            requestLayout();



            Camera.Size bestSize = null;
            List<Camera.Size> sizeList = camera.getParameters().getSupportedPreviewSizes();
            bestSize = sizeList.get(0);
            for(int i = 1; i < sizeList.size(); i++){
                if((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)){
                    bestSize = sizeList.get(i);
                }
            }

            params.setPictureSize(bestSize.width, bestSize.height);

            camera.setParameters(params);
            camera.startPreview();

            generateProjectionMatrix();
        }
    }

    // Tính toán tọa độ hiển thị trên màn hình
    private void generateProjectionMatrix() {
        float ratio = (float) this.cameraWidth / this.cameraHeight;
        final int OFFSET = 0;
        final float LEFT = -ratio;
        final float RIGHT = ratio;
        final float BOTTOM = -1;
        final float TOP = 1;
        Matrix.frustumM(projectionMatrix, OFFSET, LEFT, RIGHT, BOTTOM, TOP, Z_NEAR, Z_FAR);
    }

    public float[] getProjectionMatrix() {
        return projectionMatrix;
    }

}
