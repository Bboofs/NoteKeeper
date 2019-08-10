package com.loogs.notekeeper;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_POSITION = "com.loogs.notekeeper.NOTE_POSITION";
    public static final String ORIGINAL_NOTE_COURSE_ID = "com.loogs.notekeeper.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.loogs.notekeeper.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.loogs.notekeeper.ORIGINAL_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIscancelling;
    private String mOriginallNoteCourseId;
    private String mOriginallNoteTitle;
    private String mOriginallNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_nav);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        if(savedInstanceState == null) {
            saveOriginalNoteValues();
        } else {
            restoreOriginalNoteValues(savedInstanceState);
        }


        mTextNoteTitle = findViewById(R.id.text_note_title);
        mTextNoteText = findViewById(R.id.text_note_text);

        if(!mIsNewNote)
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

        Log.d(TAG, "onCreate");
    }

    private void restoreOriginalNoteValues(Bundle savedInstanceState) {
        mOriginallNoteCourseId = savedInstanceState.getString(ORIGINAL_NOTE_COURSE_ID);
        mOriginallNoteTitle = savedInstanceState.getString(ORIGINAL_NOTE_TITLE);
        mOriginallNoteText = savedInstanceState.getString(ORIGINAL_NOTE_TEXT);
    }

    private void saveOriginalNoteValues() {
        if(mIsNewNote)
            return;
        mOriginallNoteCourseId = mNote.getCourse().getCourseId();
        mOriginallNoteTitle = mNote.getTitle();
        mOriginallNoteText = mNote.getText();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mIscancelling) {
            Log.i(TAG, "Cancelling note at position: " + mNotePosition);
            if(mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            } else {
                storePreviousNoteValues();
            }
        } else {
            saveNote();
        }

        Log.d(TAG, "onPause");
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(mOriginallNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mOriginallNoteTitle);
        mNote.setText(mOriginallNoteText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ORIGINAL_NOTE_COURSE_ID, mOriginallNoteCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE, mOriginallNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT, mOriginallNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = courses.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIndex);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = mNotePosition == POSITION_NOT_SET;
        if(mIsNewNote) {
            createNewNote();
        }

        Log.i(TAG, "mNotePosition: " + mNotePosition);
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        //mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIscancelling = true;
            finish();
        } else if (id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNotePosition < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();

        ++mNotePosition;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        saveOriginalNoteValues();
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        invalidateOptionsMenu();
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = "Checkout what I learned in the Pluralsight course \"" +
                course.getTitle() + "\"\n" + mTextNoteText.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}
