package com.google.devrel.training.conference.spi;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiMethod.HttpMethod;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.devrel.training.conference.Constants;
import com.google.devrel.training.conference.domain.Profile;
import com.google.devrel.training.conference.form.ProfileForm;
import com.google.devrel.training.conference.form.ProfileForm.TeeShirtSize;
import com.googlecode.objectify.Key;

import static com.google.devrel.training.conference.service.OfyService.ofy;

/**
 * Defines conference APIs.
 */
@Api(name = "conference", version = "v1", scopes = { Constants.EMAIL_SCOPE }, clientIds = {
        Constants.WEB_CLIENT_ID, Constants.API_EXPLORER_CLIENT_ID }, description = "API for the Conference Central Backend application.")
public class ConferenceApi {

    /*
     * Get the display name from the user's email. For example, if the email is
     * lemoncake@example.com, then the display name becomes "lemoncake."
     */
    private static String extractDefaultDisplayNameFromEmail(String email) {
        return email == null ? null : email.substring(0, email.indexOf("@"));
    }

    /**
     * Creates or updates a Profile object associated with the given user
     * object.
     *
     * @param user
     *            A User object injected by the cloud endpoints.
     * @param profileForm
     *            A ProfileForm object sent from the client form.
     * @return Profile object just created.
     * @throws UnauthorizedException
     *             when the User object is null.
     */

    // Declare this method as a method available externally through Endpoints
    @ApiMethod(name = "saveProfile", path = "profile", httpMethod = HttpMethod.POST)
    // The request that invokes this method should provide data that
    // conforms to the fields defined in ProfileForm

    // TODO 1 Pass the ProfileForm parameter
    // TODO 2 Pass the User parameter
    public Profile saveProfile(final User user,ProfileForm profileForm) throws UnauthorizedException {

        String userId = null;
        String mainEmail = null;
        String displayName = "Your name will go here";
        TeeShirtSize teeShirtSize = TeeShirtSize.NOT_SPECIFIED;

        if(user==null){
            throw new UnauthorizedException("not authorized to use service");
        }

        userId = user.getUserId();
        mainEmail = user.getEmail();

        if(profileForm.getTeeShirtSize()!=null){
            teeShirtSize = profileForm.getTeeShirtSize();
        }

        displayName = profileForm.getDisplayName();

        if(displayName==null){
            displayName = extractDefaultDisplayNameFromEmail(mainEmail);
        }

        Profile profile = getProfile(user);
        if(profile ==null) {
            profile = new Profile(userId, displayName, mainEmail, teeShirtSize);
        }else{
            if(profile.getTeeShirtSize()!=teeShirtSize || !profile.getDisplayName().equals(displayName))
            profile.update(teeShirtSize,displayName);
        }
        // TODO 3 (In Lesson 3)
        ofy().save().entity(profile).now();
        // Save the Profile entity in the datastore

        // Return the profile
        return profile;
    }

    /**
     * Returns a Profile object associated with the given user object. The cloud
     * endpoints system automatically inject the User object.
     *
     * @param user
     *            A User object injected by the cloud endpoints.
     * @return Profile object.
     * @throws UnauthorizedException
     *             when the User object is null.
     */
    @ApiMethod(name = "getProfile", path = "profile", httpMethod = HttpMethod.GET)
    public Profile getProfile(final User user) throws UnauthorizedException {
        if (user == null) {
            throw new UnauthorizedException("Authorization required");
        }

        // TODO
        // load the Profile Entity
        String userId = ""; // TODO
        Key<Profile> key = Key.create(Profile.class,user.getUserId()); // TODO
        //Profile profile = null; // TODO load the Profile entity
        Profile profile  = ofy().load().key(key).now();
        return profile;
    }
}
