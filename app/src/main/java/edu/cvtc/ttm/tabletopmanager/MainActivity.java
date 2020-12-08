package edu.cvtc.ttm.tabletopmanager;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.cvtc.ttm.tabletopmanager.DatabaseContract.CharactersTable;

import static java.lang.String.valueOf;

public class MainActivity extends AppCompatActivity {

    EditText charName;
    DatabaseHelper dbHelper;
    ListView charList;
    Button selectedChar;
    Button removeCharacterButton;
    int deletePosition;
    boolean deleteEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        charName = findViewById(R.id.characterEditText);
        dbHelper = new DatabaseHelper(this);
        charList = findViewById(R.id.characterListView);
        deleteEnabled = false;

        final Button addCharacterButton = findViewById(R.id.addCharacterButton);
        addCharacterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newCharacterName = charName.getText().toString();

                SQLiteDatabase db = dbHelper.getWritableDatabase();

                //validate the new character name
                //cant be blank
                if (newCharacterName.matches("")) {
                    Toast.makeText(view.getContext(), "Please enter a character name", Toast.LENGTH_LONG).show();
                //We currently have limit of seven characters due to issues with deleting characters (modifying listView entries while they are offscreen?)
                } else if (charList.getCount() >= 7) {
                    Toast.makeText(view.getContext(), "You have the max amount of characters, please delete one first", Toast.LENGTH_LONG).show();
               //Finally, the character's name must be a unique one...
                } else {
                    if (dbHelper.compareNewName(db, newCharacterName)) {
                        dbHelper.newCharacter(newCharacterName, db);
                        getCharacterData();
                        charName.getText().clear();

                        hideKeyboard(MainActivity.this);
                    } else {
                        Toast.makeText(view.getContext(), "This name is already being used", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });

        //TODO: Fix all the character delete related crashes.

        // Long press deletes character but then clicks character that ends up in its position- not that bad but not good practice either
        final Button removeCharacterButton = findViewById(R.id.removeCharacterButton);
        removeCharacterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (deleteEnabled == false) {
                    deleteEnabled = true;
                    removeCharacterButton.setTextColor(Color.RED);
                    Toast.makeText(view.getContext(), "Long-Press on an item to delete it.", Toast.LENGTH_LONG).show();
                }
                else if (deleteEnabled == true) { //i don't care if the IDE prefers the shorter form this is more symmetrical  and more explicit, i like it here
                    deleteEnabled = false;
                    removeCharacterButton.setTextColor(Color.BLACK);
                    Toast.makeText(view.getContext(), "Exiting delete mode.", Toast.LENGTH_LONG).show();
                }
                else { //we should never hit this
                    Toast.makeText(view.getContext(), "Something has gone badly wrong; please contact tech support.", Toast.LENGTH_LONG).show();

                }
            }

        });

        charList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (deleteEnabled) {

                    Cursor cursor = (Cursor) charList.getItemAtPosition(position);
                    String column_name = cursor.getColumnName(0);
                    //String str = cursor.getString(cursor.getColumnIndex(0);
                    //Log.i("DB col name", "Should be: " + column_name);
                   Log.i("DB char name", "mycursor.getString(1) " + cursor.getString(0) +"   ");

                    TextView charView = charList.getChildAt(position).findViewById(R.id.idnum);
                    String deleteCharID = charView.getText().toString();

                    String deletedCharShow = "Deleted " + deleteCharID;

                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    dbHelper.deleteCharacter(deleteCharID, db);

                    getCharacterData();
                    Toast.makeText(view.getContext(), deletedCharShow, Toast.LENGTH_LONG).show();

                    deleteEnabled = false;
                    removeCharacterButton.setTextColor(Color.BLACK);



                }
                else {
                    Toast.makeText(view.getContext(), "Please click the \"Remove Character\" button if you want to delete this item.", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });



        //Make the character list clickable, and on a short click take the user to their inventory.
        charList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                ArrayList<String> selectedCharacter = dbHelper.getSelectedCharacter(db);

                String characterName = selectedCharacter.get(position);

                Intent intent = new Intent(MainActivity.this, InventoryActivity.class);
                intent.putExtra("name", characterName);
                intent.putExtra("id", position);
                startActivity(intent);
            }
        });

        getCharacterData();

    }
    // The fact that I had to build a function for this is ...unfortunate...
    // the fact that this is the best anyone in 2020 can do for this is even worse.
    // https://stackoverflow.com/questions/1109022/how-do-you-close-hide-the-android-soft-keyboard-using-java
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
        //return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCharacterData() {
        //Suck the character names out of the DB
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = dbHelper.fetchCharacterData(db);

        //and squirt them into a ListView
        ListAdapter myAdapter = new SimpleCursorAdapter(this, R.layout.character_list_display,
                cursor,
                new String[]{CharactersTable.COLUMN_CHARACTER_NAME},
                new int[]{R.id.idnum}, 0);
        charList.setAdapter(myAdapter);

    }
}