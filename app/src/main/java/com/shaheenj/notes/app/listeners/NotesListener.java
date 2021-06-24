package com.shaheenj.notes.app.listeners;

import com.shaheenj.notes.app.entities.Note;

public interface NotesListener {
    void onNoteClicked(Note note, int position);
}
