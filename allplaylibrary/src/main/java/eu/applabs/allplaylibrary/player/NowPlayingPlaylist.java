package eu.applabs.allplaylibrary.player;

import android.content.Context;

import java.util.List;

import javax.inject.Inject;

import eu.applabs.allplaylibrary.AllPlayLibrary;
import eu.applabs.allplaylibrary.R;
import eu.applabs.allplaylibrary.event.Event;
import eu.applabs.allplaylibrary.services.ServiceLibrary;
import eu.applabs.allplaylibrary.services.ServiceCategory;
import eu.applabs.allplaylibrary.services.ServicePlaylist;
import eu.applabs.allplaylibrary.data.MusicCatalog;
import eu.applabs.allplaylibrary.data.Song;
import eu.applabs.allplaylibrary.services.ServiceType;

public class NowPlayingPlaylist extends ServicePlaylist {

    @Inject
    protected Context mContext;

    @Inject
    protected MusicCatalog mMusicCatalog;

    private int mCurrentIndex = 0;

    private ServiceCategory mServiceCategory;

    public NowPlayingPlaylist() {
        AllPlayLibrary.getInstance().component().inject(this);
        mServiceCategory = new ServiceCategory(mContext.getString(R.string.category_currentplayback));

        NowPlayingServiceLibrary nowPlayingServiceLibrary = new NowPlayingServiceLibrary();
        nowPlayingServiceLibrary.addCategory(mServiceCategory);
        mMusicCatalog.addMusicLibrary(nowPlayingServiceLibrary);
    }

    public void setPlaylist(List<Song> list) {
        mServiceCategory.addPlaylist(this);

        if(list != null && list.size() > 0) {
            clear();

            for (Song song : list) {
                if(song != null) {
                    mSongList.add(song);
                }
            }
        }

        notifyObservers(new Event(Event.EventType.SERVICE_PLAYLIST_UPDATE));
    }

    public void remove(Song song) {
        if(mSongList != null) {
            for(int i = 0; i < mSongList.size(); ++i) {
                if(mSongList.get(i) == song) {
                    if(i <= mCurrentIndex) {
                        mCurrentIndex--;
                    }

                    mSongList.remove(song);
                    break;
                }
            }
        }

        notifyObservers(new Event(Event.EventType.SERVICE_PLAYLIST_UPDATE));
    }

    public void clear() {
        mServiceCategory.clearCategory();

        if(mSongList != null) {
            mCurrentIndex = 0;
            mSongList.clear();
        }

        notifyObservers(new Event(Event.EventType.SERVICE_PLAYLIST_UPDATE));
    }

    public List<Song> getPlaylistAsSongList() {
        return mSongList;
    }

    public int getCurrentSongIndex() {
        return mCurrentIndex;
    }

    public Song getCurrentSong() {
        if(mSongList != null && mCurrentIndex >= 0 && mCurrentIndex < mSongList.size()) {
            return mSongList.get(mCurrentIndex);
        }

        return null;
    }

    public Song getPrevSong() {
        if(mSongList != null && mCurrentIndex > 0) {
            mCurrentIndex--;
            notifyObservers(new Event(Event.EventType.SERVICE_PLAYLIST_UPDATE));

            return mSongList.get(mCurrentIndex);
        }

        return null;
    }

    public Song getNextSong() {
        if(mSongList != null && mSongList.size() > mCurrentIndex + 1) {
            mCurrentIndex++;
            notifyObservers(new Event(Event.EventType.SERVICE_PLAYLIST_UPDATE));

            return mSongList.get(mCurrentIndex);
        }

        return null;
    }

    @Override
    public String getPlaylistName() {
        Song song = getCurrentSong();

        if(song != null) {
            return song.getTitle();
        }

        return "";
    }

    @Override
    public String getCoverUrl() {
        Song song = getCurrentSong();

        if(song != null) {
            return song.getCoverSmall();
        }

        return "";
    }

    private class NowPlayingServiceLibrary extends ServiceLibrary {

        @Override
        public ServiceType getServiceType() {
            return ServiceType.UNDEFINED;
        }

    }

}
