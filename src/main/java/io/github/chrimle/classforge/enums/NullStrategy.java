/*
 * Copyright 2025 Chrimle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.chrimle.classforge.enums;

import org.apiguardian.api.API;

/**
 * The <em>strategy</em> to handle {@code null} during <strong>Enum</strong> deserialization. The
 * <em>default strategy</em>, with respect to {@link Enum#valueOf(Class, String)}, will {@link
 * #THROW} when {@code null} is provided.
 *
 * <h3>Use Cases</h3>
 *
 * <ul>
 *   <li><strong>FALLBACK</strong> - when the value <em>may</em> be nullable, and there is a
 *       definitive and <em>universal</em> fallback value.
 *   <li><strong>NULL</strong> - when the value <em>may</em> be nullable, but further context is
 *       needed to resolve a fallback value.
 *   <li><strong>THROW</strong> - when the value <em>shall not</em> be nullable, and an Exception
 *       shall be thrown immediately.
 * </ul>
 *
 * @since 0.11.0
 * @author Chrimle
 */
@API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
public enum NullStrategy {
  /**
   * This strategy will <em>return</em> a "fallback"/"default" value, if the provided value for
   * deserialization is {@code null}.
   *
   * @since 0.11.0
   */
  @API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
  FALLBACK,
  /**
   * This strategy will <em>return</em> {@code null}, if the provided value for deserialization is
   * {@code null}.
   *
   * @since 0.11.0
   */
  @API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
  NULL,
  /**
   * This strategy will <em>throw</em> a {@link NullPointerException}, if the provided value for
   * deserialization is {@code null}. This is the <strong>default</strong> strategy, inherited from
   * {@link Enum#valueOf(Class, String)}.
   *
   * @since 0.11.0
   */
  @API(status = API.Status.EXPERIMENTAL, since = "0.11.0")
  THROW
}
