    package com;

    import com.coremedia.iso.boxes.Container;
    import com.googlecode.mp4parser.FileDataSourceImpl;
    import com.googlecode.mp4parser.authoring.Movie;
    import com.googlecode.mp4parser.authoring.Track;
    import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
    import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
    import com.googlecode.mp4parser.authoring.tracks.CroppedTrack;

    import java.io.File;
    import java.io.FileOutputStream;
    import java.io.IOException;
    import java.nio.channels.Channels;
    import java.nio.channels.WritableByteChannel;
    import java.util.Arrays;
    import java.util.LinkedList;
    import java.util.List;


    /**
     * Created by sannies on 19.03.2015.
     */
    public class ShortenCheck {
        static class Log {

            public static void e(String s, String s1) {
            //    System.err.println(s + " -- " + s1);
            }
        }



        public static void main(String[] args) throws IOException {
            trimVideo("C:\\Users\\sannies\\Downloads\\ffff.mp4", "c:\\dev\\cut.mp4", 60, 300);
        }

        public static void trimVideo(final String srcFileDir, final String outFileDir, final double fromSecond, final double toSecond)
                throws IOException {

            if (fromSecond < 0) {
                return;
            }

            Movie movie = MovieCreator.build(new FileDataSourceImpl(srcFileDir));

            List<Track> tracks = movie.getTracks();
            Log.e("tracks size", "track size =" + tracks.size());
            movie.setTracks(new LinkedList<Track>());

            double startTime = 0, endTime = 0;


            boolean timeCorrected = false;

            // Here we try to find a track that has sync samples. Since we can only start decoding
            // at such a sample we SHOULD make sure that the start of the new fragment is exactly
            // such a frame
            for (Track track : tracks) {
                if (track.getSyncSamples() != null && track.getSyncSamples().length > 0) {
                    if (timeCorrected) {
                        // This exception here could be a false positive in case we have multiple tracks
                        // with sync samples at exactly the same positions. E.g. a single movie containing
                        // multiple qualities of the same video (Microsoft Smooth Streaming file)

                        throw new RuntimeException("The startTime has already been corrected by another track with SyncSample. Not Supported.");
                    }
                    startTime = correctTimeToNextSyncSample(track, fromSecond);
                    System.err.println("corrected starttime from " + fromSecond + " to " + startTime);
                    endTime = correctTimeToNextSyncSample(track, toSecond);
                    System.err.println( "corrected endTime from " + toSecond + " to " + endTime);
                    timeCorrected = true;
                }
            }
            Log.e("track list ", " start time=" + startTime + " end Time =" + endTime);
            for (Track track : tracks) {
                long currentSample = 0;
                double currentTime = 0;
                long startSample = -1;
                long endSample = -1;
                Log.e("TRACKLIST", "size track =" + track.getSampleDurations().length);
                for (int i = 0; i < track.getSampleDurations().length; i++) {
                    long delta = track.getSampleDurations()[i];
                    //Log.e("TRACKLIST", "entry count =" + entry.getCount());
                    for (int j = 0; j < 1; j++) {
                        // entry.getDelta() is the amount of time the current sample covers.

                        if (currentTime <= startTime) {
                            // current sample is still before the new starttime
                            startSample = currentSample;
                        }
                        if (currentTime <= endTime) {
                            // current sample is after the new start time and still before the new endtime
                            endSample = currentSample;
                        } else {
                            // current sample is after the end of the cropped video
                            if (endSample == -1) {
                                endSample = currentSample;
                            }
                            break;
                        }
                        currentTime += (double) delta / (double) track.getTrackMetaData().getTimescale();
                        currentSample++;
                    }
                }
                Log.e("TRACKLIST2", "startSample =" + startSample + " endSample=" + endSample);
                movie.addTrack(new CroppedTrack(track, startSample, endSample));
            }
            Container container = new DefaultMp4Builder().build(movie);
            final FileOutputStream fos = new FileOutputStream(new File(String.format(outFileDir)));
            final WritableByteChannel bb = Channels.newChannel(fos);
            container.writeContainer(bb);
            fos.close();

        }

        private static double correctTimeToNextSyncSample(Track track, double cutHere) {
            double[] timeOfSyncSamples = new double[track.getSyncSamples().length];
            long currentSample = 0;
            double currentTime = 0;
            for (long dur : track.getSampleDurations()) {
                for (int j = 0; j < 1; j++) {
                    if (Arrays.binarySearch(track.getSyncSamples(), currentSample + 1) >= 0) {
                        // samples always start with 1 but we start with zero therefore +1
                        timeOfSyncSamples[Arrays.binarySearch(track.getSyncSamples(), currentSample + 1)] = currentTime;
                    }
                    Log.e("", "");
                    currentTime += (double) dur / (double) track.getTrackMetaData().getTimescale();
                    currentSample++;
                }
            }
            for (double timeOfSyncSample : timeOfSyncSamples) {
                if (timeOfSyncSample > cutHere) {
                    return timeOfSyncSample;
                }
            }
            return timeOfSyncSamples[timeOfSyncSamples.length - 1];
        }

    }
