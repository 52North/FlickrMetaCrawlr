package org.meta.crawlr;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aetrion.flickr.contacts.ContactsInterface;
import com.aetrion.flickr.people.PeopleInterface;
import com.aetrion.flickr.people.User;
import com.aetrion.flickr.photos.GeoData;
import com.aetrion.flickr.photos.Note;
import com.aetrion.flickr.photos.Photo;
import com.aetrion.flickr.photos.PhotosInterface;
import com.aetrion.flickr.photos.geo.GeoInterface;
import com.aetrion.flickr.photosets.PhotosetsInterface;
import com.aetrion.flickr.tags.Tag;


public class FlickrQuerier {

    PhotoInfoDownloader downloader;
    PhotosInterface photoInterface;
    GeoInterface geoInterface;
    PeopleInterface pplInterface;
    ContactsInterface contactsInterface;
    PhotosetsInterface photosetsInterface;
    
    Map<String, User> userCache = new HashMap<String, User>();
    Map<String, String> userContactCountCache = new HashMap<String, String>(); 
    Map<String, String> userGroupSetCache = new HashMap<String, String>(); 
    
    public FlickrQuerier(PhotoInfoDownloader downloader, PhotosInterface  photoInterface, GeoInterface geoInterface, PeopleInterface pplInterface, ContactsInterface contactsInterface, PhotosetsInterface photosetsInterface) {
        this.downloader = downloader;
        this.photoInterface = photoInterface;
        this.geoInterface = geoInterface;
        this.pplInterface = pplInterface;
        this.contactsInterface = contactsInterface;
        this.photosetsInterface = photosetsInterface;
    }
    
    public void downloadPhoto(int i, Photo photo)
    {
        try {
            System.out.println("At time " + new Date(System.currentTimeMillis()).toGMTString() + ":   Starting to query Flickr for photo " + i);
            
            String photoID = photo.getId();
            
            // photo info:
            Photo photoInfo = photoInterface.getInfo(photoID, null);
            String photoTitle = photoInfo.getTitle();
            String photoUrl = photoInfo.getUrl();
            String photoIsFamilyFlag = "" + photoInfo.isFamilyFlag();
            String photoIsFriendFlag = "" + photoInfo.isFriendFlag();
            String photoDescription = photoInfo.getDescription();
            String photoLicense = photoInfo.getLicense();
            
            // tags:
            String photoTags = collectionToString(photoInfo.getTags(), " - ");
            String photoTagsCount = "" + photoInfo.getTags().size();
            
            // notes:
            String photoNotes = collectionToString(photoInfo.getNotes(), " - ");
            String photoNotesCount = "" + photoInfo.getNotes().size();
            
            // comments:
            String photoCommentsCount = "" + photoInfo.getComments();
            
            // dates:
            String photoDatePosted = "";
            if (photoInfo.getDatePosted() != null) {
                photoDatePosted += photoInfo.getDatePosted().toGMTString();
            }
            String photoDateTaken = "";
            if (photoInfo.getDateTaken() != null) {
                photoDateTaken += photoInfo.getDateTaken().toGMTString();
            }
            
            // favorites:
            //photoInterface.getFavorites(photoID, 50, 1);
            
            // empty:
            String photoIsPrimary = "";// + photoInfo.isPrimary();
            String photoMedia = ""; //photoInfo.getMedia();
            String photoMediaStatus = ""; //photoInfo.getMediaStatus();
            
            // geo:
            GeoData photoGeo = geoInterface.getLocation(photoID);
            String photoLongitude = "";
            String photoLatitude = "";
            if (photoGeo != null) {
                photoLongitude = "" + photoGeo.getLongitude();
                photoLatitude  = "" + photoGeo.getLatitude();
            }
            String photoAccuracy = "" + photoGeo.getAccuracy();
            
            // user:
            String userID = photo.getOwner().getId();
            User user;
            String userContactsCount;
            String userPhotoSetsCount;
            
            if (userCache.containsKey(userID)) {
                
                // read from caches:
                user = userCache.get(userID);
                userContactsCount = userContactCountCache.get(userID);
                userPhotoSetsCount = userGroupSetCache.get(userID);
            }
            else {
                user = pplInterface.getInfo(userID);
                userContactsCount  = "" + contactsInterface.getPublicList(userID).size();
                userPhotoSetsCount = "" + photosetsInterface.getList(userID).getPhotosets().size();
                
                // add to caches:
                userCache.put(userID, user);
                userContactCountCache.put(userID, userContactsCount);
                userGroupSetCache.put(userID, userPhotoSetsCount);
            }
            
            String userName = user.getUsername();
            String userRealName = user.getRealName();
            String userPhotoCount = "" + user.getPhotosCount();
            String userPhotoFirstDate = "";
            if (user.getPhotosFirstDate() != null) {
                userPhotoFirstDate = user.getPhotosFirstDate().toGMTString();
            }
            String userPhotoFirstDateTaken = "";
            if (user.getPhotosFirstDateTaken() != null) {
                userPhotoFirstDateTaken = user.getPhotosFirstDateTaken().toGMTString();
            }
            String userLocation = user.getLocation();
            String userBuddyIconUrl = user.getBuddyIconUrl();
            String userIsPro = "" + user.isPro();
            String userIsAdmin = "" + user.isAdmin();
            
            //
            // now write the queried data to file:
            //
            String[] dataRow = new String[] {
                    photoID, photoTitle, photoUrl, photoAccuracy, photoTags, photoTagsCount, photoCommentsCount,
                    photoDatePosted, photoDateTaken, 
                    photoDescription, photoNotes, photoNotesCount, photoMedia, photoMediaStatus, photoLicense,
                    photoIsFamilyFlag, photoIsFriendFlag, photoIsPrimary,
                    photoLongitude, photoLatitude, 
                    userID, userName, userRealName, userPhotoCount, userPhotoFirstDate, userPhotoFirstDateTaken,
                    userLocation, userBuddyIconUrl, userIsPro, userIsAdmin,
                    userContactsCount, userPhotoSetsCount};
            System.out.println("At time " + new Date(System.currentTimeMillis()).toGMTString() + ":   Writing data row for photo " + i);
            downloader.writeToFile(dataRow);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private String collectionToString(Collection c, String delimiterSign) {
        String result = "";
        
        for (Iterator iterator = c.iterator(); iterator.hasNext();) {
            Object cMember = (Object) iterator.next();
            
            if (cMember instanceof Tag) {
                Tag tag = (Tag) cMember;
                result += tag.getRaw() + delimiterSign;
            }
            else if (cMember instanceof Note) {
                Note note = (Note) cMember;
                result += note.getText() + delimiterSign;
            }
        }
        
        return result;
    }
}
