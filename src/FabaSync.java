import com.unboundid.ldap.sdk.*;

import java.util.*;


/**
 * Class description
 *
 * @author GomesNun
 * @version 201506.29001
 */
public class FabaSync {

    /**
     * Bit mask to detect if the user is disabled
     */
    private static final int DISABLED_ACCOUNT = 0x0002;

    /**
     * List of all users in the active directory
     */
    private Map<String, Person> _adPrs = new HashMap<>();

    /**
     * List of all users in the favasoft instance
     */
    private Map<String, FabaUser> _fsPrs = new HashMap<>();

    /**
     * Count all user changed in the favasoft instance
     */
    private int _changeCounter = 0;



    /**
     * This method will be called by the fabasoft use case.
     * The method is a template for the operations to be made.
     * Ducx call: SyncResult(out integer result, string srv, integer port, string user, string pass, string searchQuery)
     *
     *
     * @param srv server used to access the active directory (AD)
     * @param port port to access the AD. default 389
     * @param user username to login into the AD
     * @param pass user's password
     * @param searchQuery search query used to fetch the users in AD structure
     * @return integer number of fabasoft users changed
     */
    public int SyncResult(final String srv, final int port, final String user, final String pass, final String searchQuery) {

        // fetch users from both sides
        if( fetchActiveDirectoryUsers(srv, port, user, pass, searchQuery) > 0 ) {
            fetchFabasoftUsers();

            // synchronize data structures
            syncADTs();

            // update faba users
            updateFabaUsers();

            return _changeCounter;
        }
        return -1;
    }


    /**
     * This method will search the active directory schema and with a custom search
     * will find all the relevant users to be synchronized with fabasoft.
     * Input received from the ducxs invocation
     * Example search: "OU=Groups,OU=SG,OU=OrganismosME,DC=unisys,DC=local"
     *
     * @see LDAPConnection
     * @see SearchRequest
     * @see Filter
     */
    private int fetchActiveDirectoryUsers(final String srv, final int port, final String user, final String pass, final String searchQuery) {
        LDAPConnection connection = null;
        try {
            // connection
            connection = new LDAPConnection(srv,port,user,pass);

            // search
            Filter cnFilter = Filter.createPresenceFilter("CN");

            Filter filter = Filter.createANDFilter(cnFilter);
            SearchRequest searchRequest = new SearchRequest(searchQuery, SearchScope.SUB, filter);
            SearchResult result = connection.search(searchRequest);

            int entriesFound = result.getEntryCount();
            for (SearchResultEntry entry : result.getSearchEntries())
            {
                String g = entry.getAttributeValue("cn");
                for( String userStr : entry.getAttribute("member").getValues()) {
                    Person p = new Person(g, userStr);
                    _adPrs.put(p.getName(), p);
                }
            }

            // close connection
            connection.close();

            return entriesFound;
        } catch (LDAPException e) {
            e.printStackTrace();
            return 0;
        } finally {
            if( connection != null && connection.isConnected() ) {
                connection.close();
            }
        }
    }

    private void printActiveDirectoryUsers() {
        System.out.println("CTR---------------------------------");
        for (Person p : _adPrs.values()) {
            System.out.println(p.toString());
        }
        System.out.println("------------------------------------");
    }


    /**
     * Retrieves all relevant information from the users in the fabasoft instance
     */
    private void fetchFabasoftUsers() {

    }


    /**
     * This method will synchronize the data structures.
     * All the entries of the active directory will the iterated and if the user does not exist or have a
     * different data, the object will be replaced with one equal to the active directory.
     * The users in the active directory that exist in the fabasoft instance will be set as enable, as a way
     * to flag the ones to be disabled.
     */
    private void syncADTs() {
        Iterator it = _adPrs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Person> entry = (Map.Entry)it.next();

            // find faba user with same name && compare hash
            // if different then write all properties to the faba side
            if( !_fsPrs.containsKey(entry.getKey()) || _fsPrs.get(entry.getKey()).hashCode() == entry.getValue().hashCode() ) {
                _fsPrs.put(entry.getKey(), new FabaUser(entry.getValue()));
                _changeCounter++;
            }
            else {
                _fsPrs.get(entry.getKey()).enable();
            }
        }
    }


    /**
     * This method will make the changes in the fabasoft instance.
     * Entries will be created or its properties changed.
     */
    private void updateFabaUsers() {
        Iterator it = _fsPrs.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, FabaUser> fbUser = (Map.Entry)it.next();
            if( fbUser.getValue().changed() ) {
                // change settings on faba
            }
            else if( fbUser.getValue().isDisabled() ) {
                // disable user
            }
        }
    }


    /**
     * Checks if the user with the given account control is disabled
     * by applying the disable bit mask
     *
     * @param accountControl user account control from the ad schema
     * @return true if disabled, false otherwise
     */
    public boolean isAccountEnabled(String accountControl) {
        int userAccountControl = Integer.parseInt(accountControl);
        return ( userAccountControl & DISABLED_ACCOUNT ) == 0;
    }


    /**
     *
     * @param args
     */
    public static void main(String[] args) {


    }

}
