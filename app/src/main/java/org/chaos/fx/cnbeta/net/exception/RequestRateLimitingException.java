/*
 * Copyright 2016 Chaos
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

package org.chaos.fx.cnbeta.net.exception;

public class RequestRateLimitingException extends IllegalArgumentException {
    public RequestRateLimitingException() {
        super("Request too often, please retry after a few seconds.");
    }

    public RequestRateLimitingException(String s) {
        super(s);
    }

    public RequestRateLimitingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestRateLimitingException(Throwable cause) {
        super(cause);
    }
}
