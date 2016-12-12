/**
 * Copyright (c) 2016 Intel Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.trustedanalytics.auth.gateway.spi;

import java.util.Arrays;
import java.util.Collection;

public class CompositeAuthorizable implements Authorizable {

    private final Collection<Authorizable> children;

    public CompositeAuthorizable(Authorizable... authorizable) {
        children = Arrays.asList(authorizable);
    }

    @Override
    public void addOrganization(String orgId) throws AuthorizableGatewayException {
        for (Authorizable a: children) {
            a.addOrganization(orgId);
        }
    }

    @Override
    public void addUserToOrg(String userId, String orgId) throws AuthorizableGatewayException {
        for (Authorizable a: children) {
            a.addUserToOrg(userId, orgId);
        }
    }

    @Override
    public void removeOrganization(String orgId) throws AuthorizableGatewayException {
        for (Authorizable a: children) {
            a.removeOrganization(orgId);
        }
    }

    @Override
    public void removeUserFromOrg(String userId, String orgId) throws AuthorizableGatewayException {
        for (Authorizable a: children) {
            a.removeUserFromOrg(userId, orgId);
        }
    }

    @Override
    public void synchronize() throws AuthorizableGatewayException {
        for (Authorizable a: children) {
            a.synchronize();
        }
    }

    @Override
    public String getName() {
        return children.stream()
                .map(Authorizable::getName)
                .reduce("", (a, b) -> a + "," + b);
    }
}
