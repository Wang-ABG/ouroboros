package com.arxivai.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.arxivai.data.local.entity.HistoryEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class HistoryDao_Impl implements HistoryDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<HistoryEntity> __insertionAdapterOfHistoryEntity;

  private final SharedSQLiteStatement __preparedStmtOfRemoveFromHistory;

  private final SharedSQLiteStatement __preparedStmtOfClearHistory;

  public HistoryDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfHistoryEntity = new EntityInsertionAdapter<HistoryEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `reading_history` (`paperId`,`title`,`authors`,`categories`,`viewedAt`) VALUES (?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final HistoryEntity entity) {
        statement.bindString(1, entity.getPaperId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getAuthors());
        statement.bindString(4, entity.getCategories());
        statement.bindLong(5, entity.getViewedAt());
      }
    };
    this.__preparedStmtOfRemoveFromHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reading_history WHERE paperId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearHistory = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM reading_history";
        return _query;
      }
    };
  }

  @Override
  public Object addToHistory(final HistoryEntity entry,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfHistoryEntity.insert(entry);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object removeFromHistory(final String paperId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemoveFromHistory.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, paperId);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfRemoveFromHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearHistory(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearHistory.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfClearHistory.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<HistoryEntity>> getAllHistory() {
    final String _sql = "SELECT * FROM reading_history ORDER BY viewedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_history"}, new Callable<List<HistoryEntity>>() {
      @Override
      @NonNull
      public List<HistoryEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPaperId = CursorUtil.getColumnIndexOrThrow(_cursor, "paperId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthors = CursorUtil.getColumnIndexOrThrow(_cursor, "authors");
          final int _cursorIndexOfCategories = CursorUtil.getColumnIndexOrThrow(_cursor, "categories");
          final int _cursorIndexOfViewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "viewedAt");
          final List<HistoryEntity> _result = new ArrayList<HistoryEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final HistoryEntity _item;
            final String _tmpPaperId;
            _tmpPaperId = _cursor.getString(_cursorIndexOfPaperId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthors;
            _tmpAuthors = _cursor.getString(_cursorIndexOfAuthors);
            final String _tmpCategories;
            _tmpCategories = _cursor.getString(_cursorIndexOfCategories);
            final long _tmpViewedAt;
            _tmpViewedAt = _cursor.getLong(_cursorIndexOfViewedAt);
            _item = new HistoryEntity(_tmpPaperId,_tmpTitle,_tmpAuthors,_tmpCategories,_tmpViewedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getHistoryEntry(final String paperId,
      final Continuation<? super HistoryEntity> $completion) {
    final String _sql = "SELECT * FROM reading_history WHERE paperId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, paperId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<HistoryEntity>() {
      @Override
      @Nullable
      public HistoryEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfPaperId = CursorUtil.getColumnIndexOrThrow(_cursor, "paperId");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthors = CursorUtil.getColumnIndexOrThrow(_cursor, "authors");
          final int _cursorIndexOfCategories = CursorUtil.getColumnIndexOrThrow(_cursor, "categories");
          final int _cursorIndexOfViewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "viewedAt");
          final HistoryEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpPaperId;
            _tmpPaperId = _cursor.getString(_cursorIndexOfPaperId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthors;
            _tmpAuthors = _cursor.getString(_cursorIndexOfAuthors);
            final String _tmpCategories;
            _tmpCategories = _cursor.getString(_cursorIndexOfCategories);
            final long _tmpViewedAt;
            _tmpViewedAt = _cursor.getLong(_cursorIndexOfViewedAt);
            _result = new HistoryEntity(_tmpPaperId,_tmpTitle,_tmpAuthors,_tmpCategories,_tmpViewedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<Integer> getHistoryCount() {
    final String _sql = "SELECT COUNT(*) FROM reading_history";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"reading_history"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
