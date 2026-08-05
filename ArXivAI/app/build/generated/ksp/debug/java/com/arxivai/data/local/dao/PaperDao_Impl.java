package com.arxivai.data.local.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.arxivai.data.local.entity.PaperEntity;
import java.lang.Class;
import java.lang.Exception;
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
public final class PaperDao_Impl implements PaperDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfUpdateBookmarkStatus;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastAccessed;

  private final SharedSQLiteStatement __preparedStmtOfDeletePaper;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  private final EntityUpsertionAdapter<PaperEntity> __upsertionAdapterOfPaperEntity;

  public PaperDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfUpdateBookmarkStatus = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE papers SET isBookmarked = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateLastAccessed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE papers SET lastAccessed = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeletePaper = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM papers WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM papers";
        return _query;
      }
    };
    this.__upsertionAdapterOfPaperEntity = new EntityUpsertionAdapter<PaperEntity>(new EntityInsertionAdapter<PaperEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `papers` (`id`,`title`,`authors`,`abstract`,`summary`,`categories`,`publishedDate`,`updatedDate`,`pdfUrl`,`comment`,`isBookmarked`,`lastAccessed`,`cachedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PaperEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getAuthors());
        statement.bindString(4, entity.getAbstract());
        statement.bindString(5, entity.getSummary());
        statement.bindString(6, entity.getCategories());
        statement.bindString(7, entity.getPublishedDate());
        statement.bindString(8, entity.getUpdatedDate());
        statement.bindString(9, entity.getPdfUrl());
        statement.bindString(10, entity.getComment());
        final int _tmp = entity.isBookmarked() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getLastAccessed());
        statement.bindLong(13, entity.getCachedAt());
      }
    }, new EntityDeletionOrUpdateAdapter<PaperEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `papers` SET `id` = ?,`title` = ?,`authors` = ?,`abstract` = ?,`summary` = ?,`categories` = ?,`publishedDate` = ?,`updatedDate` = ?,`pdfUrl` = ?,`comment` = ?,`isBookmarked` = ?,`lastAccessed` = ?,`cachedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PaperEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindString(3, entity.getAuthors());
        statement.bindString(4, entity.getAbstract());
        statement.bindString(5, entity.getSummary());
        statement.bindString(6, entity.getCategories());
        statement.bindString(7, entity.getPublishedDate());
        statement.bindString(8, entity.getUpdatedDate());
        statement.bindString(9, entity.getPdfUrl());
        statement.bindString(10, entity.getComment());
        final int _tmp = entity.isBookmarked() ? 1 : 0;
        statement.bindLong(11, _tmp);
        statement.bindLong(12, entity.getLastAccessed());
        statement.bindLong(13, entity.getCachedAt());
        statement.bindString(14, entity.getId());
      }
    });
  }

  @Override
  public Object updateBookmarkStatus(final String paperId, final boolean bookmarked,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateBookmarkStatus.acquire();
        int _argIndex = 1;
        final int _tmp = bookmarked ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
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
          __preparedStmtOfUpdateBookmarkStatus.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateLastAccessed(final String paperId, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastAccessed.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
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
          __preparedStmtOfUpdateLastAccessed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deletePaper(final String paperId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeletePaper.acquire();
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
          __preparedStmtOfDeletePaper.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
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
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertPaper(final PaperEntity paper, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfPaperEntity.upsert(paper);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertPapers(final List<PaperEntity> papers,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfPaperEntity.upsert(papers);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PaperEntity>> getAllPapers() {
    final String _sql = "SELECT * FROM papers ORDER BY lastAccessed DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"papers"}, new Callable<List<PaperEntity>>() {
      @Override
      @NonNull
      public List<PaperEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthors = CursorUtil.getColumnIndexOrThrow(_cursor, "authors");
          final int _cursorIndexOfAbstract = CursorUtil.getColumnIndexOrThrow(_cursor, "abstract");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfCategories = CursorUtil.getColumnIndexOrThrow(_cursor, "categories");
          final int _cursorIndexOfPublishedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedDate");
          final int _cursorIndexOfUpdatedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedDate");
          final int _cursorIndexOfPdfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfUrl");
          final int _cursorIndexOfComment = CursorUtil.getColumnIndexOrThrow(_cursor, "comment");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfLastAccessed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAccessed");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<PaperEntity> _result = new ArrayList<PaperEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaperEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthors;
            _tmpAuthors = _cursor.getString(_cursorIndexOfAuthors);
            final String _tmpAbstract;
            _tmpAbstract = _cursor.getString(_cursorIndexOfAbstract);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpCategories;
            _tmpCategories = _cursor.getString(_cursorIndexOfCategories);
            final String _tmpPublishedDate;
            _tmpPublishedDate = _cursor.getString(_cursorIndexOfPublishedDate);
            final String _tmpUpdatedDate;
            _tmpUpdatedDate = _cursor.getString(_cursorIndexOfUpdatedDate);
            final String _tmpPdfUrl;
            _tmpPdfUrl = _cursor.getString(_cursorIndexOfPdfUrl);
            final String _tmpComment;
            _tmpComment = _cursor.getString(_cursorIndexOfComment);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final long _tmpLastAccessed;
            _tmpLastAccessed = _cursor.getLong(_cursorIndexOfLastAccessed);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new PaperEntity(_tmpId,_tmpTitle,_tmpAuthors,_tmpAbstract,_tmpSummary,_tmpCategories,_tmpPublishedDate,_tmpUpdatedDate,_tmpPdfUrl,_tmpComment,_tmpIsBookmarked,_tmpLastAccessed,_tmpCachedAt);
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
  public Flow<List<PaperEntity>> getPapersByCategory(final String category) {
    final String _sql = "SELECT * FROM papers WHERE categories LIKE '%' || ? || '%' ORDER BY publishedDate DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, category);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"papers"}, new Callable<List<PaperEntity>>() {
      @Override
      @NonNull
      public List<PaperEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthors = CursorUtil.getColumnIndexOrThrow(_cursor, "authors");
          final int _cursorIndexOfAbstract = CursorUtil.getColumnIndexOrThrow(_cursor, "abstract");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfCategories = CursorUtil.getColumnIndexOrThrow(_cursor, "categories");
          final int _cursorIndexOfPublishedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedDate");
          final int _cursorIndexOfUpdatedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedDate");
          final int _cursorIndexOfPdfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfUrl");
          final int _cursorIndexOfComment = CursorUtil.getColumnIndexOrThrow(_cursor, "comment");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfLastAccessed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAccessed");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<PaperEntity> _result = new ArrayList<PaperEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaperEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthors;
            _tmpAuthors = _cursor.getString(_cursorIndexOfAuthors);
            final String _tmpAbstract;
            _tmpAbstract = _cursor.getString(_cursorIndexOfAbstract);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpCategories;
            _tmpCategories = _cursor.getString(_cursorIndexOfCategories);
            final String _tmpPublishedDate;
            _tmpPublishedDate = _cursor.getString(_cursorIndexOfPublishedDate);
            final String _tmpUpdatedDate;
            _tmpUpdatedDate = _cursor.getString(_cursorIndexOfUpdatedDate);
            final String _tmpPdfUrl;
            _tmpPdfUrl = _cursor.getString(_cursorIndexOfPdfUrl);
            final String _tmpComment;
            _tmpComment = _cursor.getString(_cursorIndexOfComment);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final long _tmpLastAccessed;
            _tmpLastAccessed = _cursor.getLong(_cursorIndexOfLastAccessed);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new PaperEntity(_tmpId,_tmpTitle,_tmpAuthors,_tmpAbstract,_tmpSummary,_tmpCategories,_tmpPublishedDate,_tmpUpdatedDate,_tmpPdfUrl,_tmpComment,_tmpIsBookmarked,_tmpLastAccessed,_tmpCachedAt);
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
  public Object getPaperById(final String id, final Continuation<? super PaperEntity> $completion) {
    final String _sql = "SELECT * FROM papers WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PaperEntity>() {
      @Override
      @Nullable
      public PaperEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthors = CursorUtil.getColumnIndexOrThrow(_cursor, "authors");
          final int _cursorIndexOfAbstract = CursorUtil.getColumnIndexOrThrow(_cursor, "abstract");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfCategories = CursorUtil.getColumnIndexOrThrow(_cursor, "categories");
          final int _cursorIndexOfPublishedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedDate");
          final int _cursorIndexOfUpdatedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedDate");
          final int _cursorIndexOfPdfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfUrl");
          final int _cursorIndexOfComment = CursorUtil.getColumnIndexOrThrow(_cursor, "comment");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfLastAccessed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAccessed");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final PaperEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthors;
            _tmpAuthors = _cursor.getString(_cursorIndexOfAuthors);
            final String _tmpAbstract;
            _tmpAbstract = _cursor.getString(_cursorIndexOfAbstract);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpCategories;
            _tmpCategories = _cursor.getString(_cursorIndexOfCategories);
            final String _tmpPublishedDate;
            _tmpPublishedDate = _cursor.getString(_cursorIndexOfPublishedDate);
            final String _tmpUpdatedDate;
            _tmpUpdatedDate = _cursor.getString(_cursorIndexOfUpdatedDate);
            final String _tmpPdfUrl;
            _tmpPdfUrl = _cursor.getString(_cursorIndexOfPdfUrl);
            final String _tmpComment;
            _tmpComment = _cursor.getString(_cursorIndexOfComment);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final long _tmpLastAccessed;
            _tmpLastAccessed = _cursor.getLong(_cursorIndexOfLastAccessed);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _result = new PaperEntity(_tmpId,_tmpTitle,_tmpAuthors,_tmpAbstract,_tmpSummary,_tmpCategories,_tmpPublishedDate,_tmpUpdatedDate,_tmpPdfUrl,_tmpComment,_tmpIsBookmarked,_tmpLastAccessed,_tmpCachedAt);
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
  public Flow<List<PaperEntity>> searchPapers(final String query) {
    final String _sql = "SELECT * FROM papers WHERE title LIKE '%' || ? || '%' OR authors LIKE '%' || ? || '%' OR abstract LIKE '%' || ? || '%'";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 3);
    int _argIndex = 1;
    _statement.bindString(_argIndex, query);
    _argIndex = 2;
    _statement.bindString(_argIndex, query);
    _argIndex = 3;
    _statement.bindString(_argIndex, query);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"papers"}, new Callable<List<PaperEntity>>() {
      @Override
      @NonNull
      public List<PaperEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfAuthors = CursorUtil.getColumnIndexOrThrow(_cursor, "authors");
          final int _cursorIndexOfAbstract = CursorUtil.getColumnIndexOrThrow(_cursor, "abstract");
          final int _cursorIndexOfSummary = CursorUtil.getColumnIndexOrThrow(_cursor, "summary");
          final int _cursorIndexOfCategories = CursorUtil.getColumnIndexOrThrow(_cursor, "categories");
          final int _cursorIndexOfPublishedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "publishedDate");
          final int _cursorIndexOfUpdatedDate = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedDate");
          final int _cursorIndexOfPdfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "pdfUrl");
          final int _cursorIndexOfComment = CursorUtil.getColumnIndexOrThrow(_cursor, "comment");
          final int _cursorIndexOfIsBookmarked = CursorUtil.getColumnIndexOrThrow(_cursor, "isBookmarked");
          final int _cursorIndexOfLastAccessed = CursorUtil.getColumnIndexOrThrow(_cursor, "lastAccessed");
          final int _cursorIndexOfCachedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "cachedAt");
          final List<PaperEntity> _result = new ArrayList<PaperEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PaperEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final String _tmpAuthors;
            _tmpAuthors = _cursor.getString(_cursorIndexOfAuthors);
            final String _tmpAbstract;
            _tmpAbstract = _cursor.getString(_cursorIndexOfAbstract);
            final String _tmpSummary;
            _tmpSummary = _cursor.getString(_cursorIndexOfSummary);
            final String _tmpCategories;
            _tmpCategories = _cursor.getString(_cursorIndexOfCategories);
            final String _tmpPublishedDate;
            _tmpPublishedDate = _cursor.getString(_cursorIndexOfPublishedDate);
            final String _tmpUpdatedDate;
            _tmpUpdatedDate = _cursor.getString(_cursorIndexOfUpdatedDate);
            final String _tmpPdfUrl;
            _tmpPdfUrl = _cursor.getString(_cursorIndexOfPdfUrl);
            final String _tmpComment;
            _tmpComment = _cursor.getString(_cursorIndexOfComment);
            final boolean _tmpIsBookmarked;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsBookmarked);
            _tmpIsBookmarked = _tmp != 0;
            final long _tmpLastAccessed;
            _tmpLastAccessed = _cursor.getLong(_cursorIndexOfLastAccessed);
            final long _tmpCachedAt;
            _tmpCachedAt = _cursor.getLong(_cursorIndexOfCachedAt);
            _item = new PaperEntity(_tmpId,_tmpTitle,_tmpAuthors,_tmpAbstract,_tmpSummary,_tmpCategories,_tmpPublishedDate,_tmpUpdatedDate,_tmpPdfUrl,_tmpComment,_tmpIsBookmarked,_tmpLastAccessed,_tmpCachedAt);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
