/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.tasks;

import org.gradle.api.Incubating;
import org.gradle.internal.HasInternalProtocol;

/**
 * Describes a property of a task that contains zero or more files.
 *
 * @since 3.1
 */
@Incubating
@HasInternalProtocol
public interface TaskFilePropertyBuilder extends TaskPropertyBuilder {
    /**
     * Sets which part of the path of files should be considered during up-to-date checks.
     *
     * @since 3.1
     */
    TaskFilePropertyBuilder withPathSensitivity(PathSensitivity sensitivity);
}
