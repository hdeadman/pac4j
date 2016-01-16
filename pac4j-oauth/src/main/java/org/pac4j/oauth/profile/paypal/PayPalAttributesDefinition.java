/*
  Copyright 2012 - 2015 pac4j organization

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */
package org.pac4j.oauth.profile.paypal;

import org.pac4j.core.profile.AttributesDefinition;
import org.pac4j.core.profile.converter.Converters;
import org.pac4j.oauth.profile.converter.JsonConverter;

import java.util.Arrays;

/**
 * This class defines the attributes of the PayPal profile.
 * 
 * @author Jerome Leleu
 * @since 1.4.2
 */
public class PayPalAttributesDefinition extends AttributesDefinition {
    
    public static final String ADDRESS = "address";
    public static final String FAMILY_NAME = "family_name";
    public static final String LANGUAGE = "language";
    public static final String LOCALE = "locale";
    public static final String ZONEINFO = "zoneinfo";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String GIVEN_NAME = "given_name";
    
    public PayPalAttributesDefinition() {
        Arrays.stream(new String[] {FAMILY_NAME, ZONEINFO, NAME, EMAIL, GIVEN_NAME}).forEach(a -> primary(a, Converters.stringConverter));
        primary(ADDRESS, new JsonConverter<>(PayPalAddress.class));
        primary(LANGUAGE, Converters.localeConverter);
        primary(LOCALE, Converters.localeConverter);
    }
}
