package net.dean.jraw.http;

import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.cookie.BasicMaxAgeHandler;

import java.util.Date;

/**
 * This class is responsible for handling the "secure_session" cookie set by Reddit. This cookie needs special treatment
 * because the Max-Age value is actually the negative value of time in Unix epoch seconds that the cookie expires
 * (for example: "-1411177097"). This class simply sets the expire date on the cookie to the absolute value of the given
 * {@link SetCookie}.
 */
public class RedditMaxAgeHandler extends BasicMaxAgeHandler {
    @Override
    public void parse(SetCookie cookie, String value) throws MalformedCookieException {
        long longVal = Long.valueOf(value);
        if (longVal >= 0) {
            super.parse(cookie, value);
            return;
        }
        longVal = Math.abs(longVal);

        cookie.setExpiryDate(new Date(longVal));
    }
}
