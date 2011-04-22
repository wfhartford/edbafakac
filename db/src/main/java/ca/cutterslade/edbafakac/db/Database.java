package ca.cutterslade.edbafakac.db;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.MongoURI;

public final class Database {

  private static final ConcurrentMap<Configuration, Database> INSTANCES = new MapMaker().weakKeys().makeMap();

  public static Database getInstance(final Configuration configuration) throws UnknownHostException {
    Database instance = INSTANCES.get(configuration);
    if (null == instance) {
      instance = new Database(configuration);
      final Database old = INSTANCES.putIfAbsent(configuration, instance);
      if (null != old) {
        instance = old;
      }
    }
    return instance;
  }

  public static Database getExistingInstance(final Configuration configuration) {
    final Database database = INSTANCES.get(configuration);
    Preconditions.checkArgument(null != database, "No database with specified configuration: %s", configuration);
    database.readLock.lock();
    try {
      Preconditions.checkArgument(!database.closed,
          "Databsae with specified configuration has been closed: %s", configuration);
    }
    finally {
      database.readLock.unlock();
    }
    return database;
  }

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private final Lock readLock = lock.readLock();

  private final Lock writeLock = lock.writeLock();

  private final Configuration configuration;

  private boolean closed;

  private final Mongo mongo;

  private final DB mongoDb;

  private final DBCollection usersCollection;

  private final DBCollection typesCollection;

  private final DBCollection fieldsCollection;

  private final DBCollection actionsCollection;

  private final DBCollection itemsCollection;

  private final DBCollection logCollection;

  private Database(final Configuration configuration) throws UnknownHostException {
    this.configuration = configuration;
    mongo = new Mongo(new MongoURI(configuration.getMongoUri()));
    mongoDb = mongo.getDB(configuration.getDbName());
    usersCollection = mongoDb.getCollection(configuration.getUsersCollection());
    typesCollection = mongoDb.getCollection(configuration.getTypesCollection());
    fieldsCollection = mongoDb.getCollection(configuration.getFieldsCollection());
    actionsCollection = mongoDb.getCollection(configuration.getActionsCollection());
    itemsCollection = mongoDb.getCollection(configuration.getItemsCollection());
    logCollection = mongoDb.getCollection(configuration.getLogCollection());
  }

  public void close() {
    writeLock.lock();
    try {
      if (!closed) {
        mongo.close();
        closed = true;
      }
    }
    finally {
      writeLock.unlock();
    }
  }

  private <F, T> T locked(final Function<F, T> function, final F input) {
    readLock.lock();
    try {
      return function.apply(input);
    }
    finally {
      readLock.unlock();
    }
  }

  public Configuration getConfiguration() {
    return configuration;
  }

}
