/*
 * Copyright Terracotta, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ehcache.clustered.lock.server;

import org.ehcache.clustered.lock.common.LockMessaging.LockOperation;
import org.ehcache.clustered.lock.common.LockMessaging.LockTransition;
import org.terracotta.entity.PassiveServerEntity;

class VoltronReadWriteLockPassiveEntity implements PassiveServerEntity<LockOperation, LockTransition> {

  static final VoltronReadWriteLockPassiveEntity INSTANCE = new VoltronReadWriteLockPassiveEntity();

  private VoltronReadWriteLockPassiveEntity() {
    //singleton
  }

  @Override
  public void invoke(LockOperation message) {
    throw new AssertionError("Unexpected message at passive " + message);
  }

  @Override
  public void startSyncEntity() {}

  @Override
  public void endSyncEntity() {}

  @Override
  public void startSyncConcurrencyKey(int concurrencyKey) {}

  @Override
  public void endSyncConcurrencyKey(int concurrencyKey) {}

  @Override
  public void createNew() {}

  @Override
  public void loadExisting() {}

  @Override
  public void destroy() {}
}
