package net.dean.jraw.paginators;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Thing;

/**
 * This class is used to paginate through a list of fullnames (can be comments, submissions, or subreddits)
 */
public class FullnamesPaginator {
    private String[] fullnames;
    private int index;
    private int count = 25;
    RedditClient reddit;

    /**
     * Instantiates a new FullnamesPaginator that will iterate through a list of fullnames
     * @param creator The RedditClient that will be used to send HTTP requests
     * @param fullnames The list of fullnames to iterate through
     */
    public FullnamesPaginator(RedditClient creator, String[] fullnames) {
        this.fullnames = fullnames;
        this.reddit = creator;
    }

    /* API limit is 100*/
    public void setLimit(int i){
        count = i;
        if(count > 100){
            count = 100;
        }
    }

    public boolean hasNext(){
        return (index < fullnames.length);
    }

    /* Override next to utilize RedditClient.get() instead of the default paginator code*/
    public Listing<Thing> next() {
        if(count > fullnames.length){
            index = fullnames.length;
            return reddit.get(fullnames);
        } else {
            String[] toGet = new String[count];

            int target = index + count;
            if (target > fullnames.length - 1) target = fullnames.length - 1;
            int current = 0;
            for (int i = index; i < target; i++) {
                toGet[current] = fullnames[i];
                current++;
            }
            index = target;
            return reddit.get(toGet);
        }
    }

}
