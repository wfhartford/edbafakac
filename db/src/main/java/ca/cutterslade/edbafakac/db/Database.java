package ca.cutterslade.edbafakac.db;

import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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
      if (null == old) {
        instance.init();
      }
      else {
        instance = old;
      }
    }
    return instance;
  }

  public static Database getExistingInstance(final Configuration configuration) {
    return INSTANCES.get(configuration);
  }

  private final ReadWriteLock lock = new ReentrantReadWriteLock();

  private final Configuration configuration;

  private boolean initialized;

  private boolean closed;

  private Mongo mongo;

  private DBCollection usersCollection;

  private DBCollection configCollection;

  private DBCollection itemsCollection;

  private DBCollection logCollection;

  private Database(final Configuration configuration) {
    this.configuration = configuration;
  }

  private void init() throws UnknownHostException {
    lock.writeLock().lock();
    try {
      Preconditions.checkState(!initialized);
      mongo = new Mongo(new MongoURI(configuration.getMongoUri()));
      final DB mongoDb = mongo.getDB(configuration.getDbName());
      usersCollection = mongoDb.getCollection(configuration.getUsersCollection());
      configCollection = mongoDb.getCollection(configuration.getConfigCollection());
      itemsCollection = mongoDb.getCollection(configuration.getItemsCollection());
      logCollection = mongoDb.getCollection(configuration.getLogCollection());
      initialized = true;
    }
    finally {
      lock.writeLock().unlock();
    }
  }

  public void close() {
    lock.writeLock().lock();
    try {
      if (!closed) {
        Preconditions.checkState(initialized);
        mongo.close();
        mongo = null;
        usersCollection = null;
        configCollection = null;
        itemsCollection = null;
        logCollection = null;
        closed = true;
      }
    }
    finally {
      lock.writeLock().unlock();
    }
  }

  public Configuration getConfiguration() {
    return configuration;
  }

}
