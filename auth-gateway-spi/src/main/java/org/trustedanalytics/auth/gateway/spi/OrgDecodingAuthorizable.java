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

import java.util.function.Function;

public class OrgDecodingAuthorizable implements Authorizable {

    private final Authorizable delegee;
    private final Function<String, String> orgDecoder;

    public OrgDecodingAuthorizable(Authorizable delegee, Function<String, String> orgDecoder) {
        this.delegee = delegee;
        this.orgDecoder = orgDecoder;
    }

    @Override
    public void addOrganization(String orgId) throws AuthorizableGatewayException {
        delegee.addOrganization(orgDecoder.apply(orgId));
    }

    @Override
    public void addUserToOrg(String userId, String orgId) throws AuthorizableGatewayException {
        delegee.addUserToOrg(userId, orgDecoder.apply(orgId));
    }

    @Override
    public void removeOrganization(String orgId) throws AuthorizableGatewayException {
        delegee.removeOrganization(orgDecoder.apply(orgId));
    }

    @Override
    public void removeUserFromOrg(String userId, String orgId) throws AuthorizableGatewayException {
        delegee.removeUserFromOrg(userId, orgDecoder.apply(orgId));
    }

    @Override
    public void synchronize() throws AuthorizableGatewayException {
        delegee.synchronize();
    }

    @Override
    public String getName() {
        return delegee.getName();
    }
}
