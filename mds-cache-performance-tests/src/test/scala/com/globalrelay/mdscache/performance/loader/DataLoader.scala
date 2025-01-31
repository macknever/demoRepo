package com.globalrelay.mdscache.performance.loader

/**
 * The trait pre-populates data in Aerospike.
 */
trait DataLoader {
  def load(): Unit
}
