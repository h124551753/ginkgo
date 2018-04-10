package debug;

import android.content.Intent;
import android.view.View;

import com.molmc.ginkgo.basic.base.BaseActivity;
import com.molmc.ginkgo.scanner.R;
import com.molmc.ginkgo.scanner.activity.CaptureActivity;

import static com.molmc.ginkgo.scanner.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;

/**
 * Created by 10295 on 2018/3/13.
 * 二维码扫描组件DEBUG版本MainActivity
 */

public class ScannerMainActivity extends BaseActivity {
    @Override
    protected int setContentView() {
        return R.layout.scanner_activity_main;
    }

    @Override
    protected void start() {

    }

    public void toCaptureActivity(View view) {
        startActivityForResult(CaptureActivity.class);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == DEFAULT_REQUEST_CODE) {
            String dataStr = data.getStringExtra(INTENT_EXTRA_KEY_QR_SCAN);
            showToast("扫描成功:" + dataStr);
        }
    }
}
