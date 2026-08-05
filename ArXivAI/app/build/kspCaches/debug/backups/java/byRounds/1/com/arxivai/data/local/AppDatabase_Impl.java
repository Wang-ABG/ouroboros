package com.arxivai.data.local;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.arxivai.data.local.dao.BookmarkDao;
import com.arxivai.data.local.dao.BookmarkDao_Impl;
import com.arxivai.data.local.dao.HistoryDao;
import com.arxivai.data.local.dao.HistoryDao_Impl;
import com.arxivai.data.local.dao.PaperDao;
import com.arxivai.data.local.dao.PaperDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile PaperDao _paperDao;

  private volatile BookmarkDao _bookmarkDao;

  private volatile HistoryDao _historyDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `papers` (`id` TEXT NOT NULL, `title` TEXT NOT NULL, `authors` TEXT NOT NULL, `abstract` TEXT NOT NULL, `summary` TEXT NOT NULL, `categories` TEXT NOT NULL, `publishedDate` TEXT NOT NULL, `updatedDate` TEXT NOT NULL, `pdfUrl` TEXT NOT NULL, `comment` TEXT NOT NULL, `isBookmarked` INTEGER NOT NULL, `lastAccessed` INTEGER NOT NULL, `cachedAt` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `bookmarks` (`paperId` TEXT NOT NULL, `title` TEXT NOT NULL, `authors` TEXT NOT NULL, `categories` TEXT NOT NULL, `addedAt` INTEGER NOT NULL, `notes` TEXT NOT NULL, PRIMARY KEY(`paperId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `reading_history` (`paperId` TEXT NOT NULL, `title` TEXT NOT NULL, `authors` TEXT NOT NULL, `categories` TEXT NOT NULL, `viewedAt` INTEGER NOT NULL, PRIMARY KEY(`paperId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '858f2e8e8c89ffcfc217d46f2019ee00')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `papers`");
        db.execSQL("DROP TABLE IF EXISTS `bookmarks`");
        db.execSQL("DROP TABLE IF EXISTS `reading_history`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPapers = new HashMap<String, TableInfo.Column>(13);
        _columnsPapers.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("authors", new TableInfo.Column("authors", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("abstract", new TableInfo.Column("abstract", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("summary", new TableInfo.Column("summary", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("categories", new TableInfo.Column("categories", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("publishedDate", new TableInfo.Column("publishedDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("updatedDate", new TableInfo.Column("updatedDate", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("pdfUrl", new TableInfo.Column("pdfUrl", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("comment", new TableInfo.Column("comment", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("isBookmarked", new TableInfo.Column("isBookmarked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("lastAccessed", new TableInfo.Column("lastAccessed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPapers.put("cachedAt", new TableInfo.Column("cachedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPapers = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPapers = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPapers = new TableInfo("papers", _columnsPapers, _foreignKeysPapers, _indicesPapers);
        final TableInfo _existingPapers = TableInfo.read(db, "papers");
        if (!_infoPapers.equals(_existingPapers)) {
          return new RoomOpenHelper.ValidationResult(false, "papers(com.arxivai.data.local.entity.PaperEntity).\n"
                  + " Expected:\n" + _infoPapers + "\n"
                  + " Found:\n" + _existingPapers);
        }
        final HashMap<String, TableInfo.Column> _columnsBookmarks = new HashMap<String, TableInfo.Column>(6);
        _columnsBookmarks.put("paperId", new TableInfo.Column("paperId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("authors", new TableInfo.Column("authors", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("categories", new TableInfo.Column("categories", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("addedAt", new TableInfo.Column("addedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBookmarks.put("notes", new TableInfo.Column("notes", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysBookmarks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesBookmarks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBookmarks = new TableInfo("bookmarks", _columnsBookmarks, _foreignKeysBookmarks, _indicesBookmarks);
        final TableInfo _existingBookmarks = TableInfo.read(db, "bookmarks");
        if (!_infoBookmarks.equals(_existingBookmarks)) {
          return new RoomOpenHelper.ValidationResult(false, "bookmarks(com.arxivai.data.local.entity.BookmarkEntity).\n"
                  + " Expected:\n" + _infoBookmarks + "\n"
                  + " Found:\n" + _existingBookmarks);
        }
        final HashMap<String, TableInfo.Column> _columnsReadingHistory = new HashMap<String, TableInfo.Column>(5);
        _columnsReadingHistory.put("paperId", new TableInfo.Column("paperId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingHistory.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingHistory.put("authors", new TableInfo.Column("authors", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingHistory.put("categories", new TableInfo.Column("categories", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsReadingHistory.put("viewedAt", new TableInfo.Column("viewedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysReadingHistory = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesReadingHistory = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoReadingHistory = new TableInfo("reading_history", _columnsReadingHistory, _foreignKeysReadingHistory, _indicesReadingHistory);
        final TableInfo _existingReadingHistory = TableInfo.read(db, "reading_history");
        if (!_infoReadingHistory.equals(_existingReadingHistory)) {
          return new RoomOpenHelper.ValidationResult(false, "reading_history(com.arxivai.data.local.entity.HistoryEntity).\n"
                  + " Expected:\n" + _infoReadingHistory + "\n"
                  + " Found:\n" + _existingReadingHistory);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "858f2e8e8c89ffcfc217d46f2019ee00", "8b2df4a1957e6c3228af32b70a133643");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "papers","bookmarks","reading_history");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `papers`");
      _db.execSQL("DELETE FROM `bookmarks`");
      _db.execSQL("DELETE FROM `reading_history`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PaperDao.class, PaperDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(BookmarkDao.class, BookmarkDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(HistoryDao.class, HistoryDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PaperDao paperDao() {
    if (_paperDao != null) {
      return _paperDao;
    } else {
      synchronized(this) {
        if(_paperDao == null) {
          _paperDao = new PaperDao_Impl(this);
        }
        return _paperDao;
      }
    }
  }

  @Override
  public BookmarkDao bookmarkDao() {
    if (_bookmarkDao != null) {
      return _bookmarkDao;
    } else {
      synchronized(this) {
        if(_bookmarkDao == null) {
          _bookmarkDao = new BookmarkDao_Impl(this);
        }
        return _bookmarkDao;
      }
    }
  }

  @Override
  public HistoryDao historyDao() {
    if (_historyDao != null) {
      return _historyDao;
    } else {
      synchronized(this) {
        if(_historyDao == null) {
          _historyDao = new HistoryDao_Impl(this);
        }
        return _historyDao;
      }
    }
  }
}
