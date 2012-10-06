package org.qii.weiciyuan.ui.send;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;
import org.qii.weiciyuan.R;
import org.qii.weiciyuan.bean.CommentBean;
import org.qii.weiciyuan.bean.MessageBean;
import org.qii.weiciyuan.dao.send.CommentNewMsgDao;
import org.qii.weiciyuan.support.error.WeiboException;
import org.qii.weiciyuan.support.utils.GlobalContext;

/**
 * User: Jiang Qi
 * Date: 12-8-2
 */
public class CommentNewActivity extends AbstractNewActivity<CommentBean> {

    private String id;
    private String token;
    private MenuItem enableCommentOri;

    private MessageBean msg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setTitle(R.string.comments);
        getActionBar().setSubtitle(GlobalContext.getInstance().getCurrentAccountName());

        token = getIntent().getStringExtra("token");
        id = getIntent().getStringExtra("id");
        msg = (MessageBean) getIntent().getSerializableExtra("msg");

        getActionBar().setTitle(getString(R.string.comments));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.commentnewactivity_menu, menu);
        enableCommentOri = menu.findItem(R.id.menu_enable_ori_comment);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (msg != null && msg.getRetweeted_status() != null) {
           enableCommentOri.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected boolean canSend() {

        boolean haveContent = !TextUtils.isEmpty(getEditTextView().getText().toString());
        boolean haveToken = !TextUtils.isEmpty(token);
        boolean contentNumBelow140 = (getEditTextView().getText().toString().length() < 140);

        if (haveContent && haveToken && contentNumBelow140) {
            return true;
        } else {
            if (!haveContent && !haveToken) {
                Toast.makeText(this, getString(R.string.content_cant_be_empty_and_dont_have_account), Toast.LENGTH_SHORT).show();
            } else if (!haveContent) {
                getEditTextView().setError(getString(R.string.content_cant_be_empty));
            } else if (!haveToken) {
                Toast.makeText(this, getString(R.string.dont_have_account), Toast.LENGTH_SHORT).show();
            }

            if (!contentNumBelow140) {
                getEditTextView().setError(getString(R.string.content_words_number_too_many));
            }

        }

        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive())
                    imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
                finish();
                break;

            case R.id.menu_enable_ori_comment:
                if (enableCommentOri.isChecked()) {
                    enableCommentOri.setChecked(false);
                } else {
                    enableCommentOri.setChecked(true);
                }
                break;

        }
        return true;
    }

    @Override
    protected CommentBean sendData() throws WeiboException {
        CommentNewMsgDao dao = new CommentNewMsgDao(token, id, ((EditText) findViewById(R.id.status_new_content)).getText().toString());
        if (enableCommentOri.isChecked()) {
            dao.enableComment_ori(true);
        } else {
            dao.enableComment_ori(false);
        }

        return dao.sendNewMsg();
    }


}
