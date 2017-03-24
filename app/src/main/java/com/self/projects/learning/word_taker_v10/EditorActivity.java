package com.self.projects.learning.word_taker_v10;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EditorActivity extends AppCompatActivity {
    private String action;
    private EditText editor,editorDesc;
    private String noteFilter;
    private String oldText, oldDesc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = (EditText) findViewById(R.id.editText);
        editorDesc = (EditText) findViewById(R.id.editDesc);

        Intent intent = getIntent();

        Uri uri = intent.getParcelableExtra(NotesProvider.CONTENT_ITEM_TYPE);

        if(uri == null){
            action = Intent.ACTION_INSERT;
            setTitle(getString(R.string.new_note));
        } else {
                action = Intent.ACTION_EDIT;
            setTitle(getString(R.string.edit_note));
            noteFilter = DBOpenHelper.NOTE_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,DBOpenHelper.ALL_COLUMNS, noteFilter, null, null);

            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_TEXT));
            oldDesc = cursor.getString(cursor.getColumnIndex(DBOpenHelper.NOTE_DESC));
            editor.setText(oldText);
            editor.requestFocus();
            editorDesc.setText(oldDesc);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        switch(item.getItemId()){
            case android.R.id.home:
                //finishEditing();
                finish();
                break;
            case R.id.action_save:
                finishEditing();
                break;
        }
        return false;
    }

    private void deleteNote() {
        getContentResolver().delete(NotesProvider.CONTENT_URI, noteFilter, null);
        Toast.makeText(this,
                getString(R.string.note_deleted),
                Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void finishEditing() {
        String newText = editor.getText().toString().trim();
        String newDesc = editorDesc.getText().toString().trim();

        //if the title is blank, show an alert message and stop the flow
        if (newText.length() == 0) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.NoteSave_Failed)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        }

        switch(action) {
            case Intent.ACTION_INSERT:
                insertNote(newText,newDesc);
                break;
            case Intent.ACTION_EDIT:
                if (oldText.equals(newText) &&  oldDesc.equals(newDesc)){
                    setResult(RESULT_CANCELED);
                } else {
                    updateNote(newText,newDesc);
                }

        }
        finish();
    }

    private void updateNote(String noteText, String noteDesc) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.NOTE_DESC, noteDesc);
        getContentResolver().update(NotesProvider.CONTENT_URI, values, noteFilter, null);
        Toast.makeText(this, getString(R.string.note_updated), Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void insertNote(String noteText, String noteDesc) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.NOTE_DESC, noteDesc);
        getContentResolver().insert(NotesProvider.CONTENT_URI, values);
        setResult(RESULT_OK);
    }

}
