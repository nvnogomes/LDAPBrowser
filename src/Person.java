import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class holds relevant information of the user found
 * in the active directory structure.
 *
 * @author GomesNun
 * @version 201507.03001
 */
public class Person implements Comparable {

    /**
     * Name of the person
     */
    protected String _name;

    /**
     * First organisation unit found in the user AD structure
     */
    protected String _orgUnit;

    /**
     * First faba group found in the user AD structure
     */
    protected String _fbGrp;


    public Person() {
    }

    /**
     * Default constructor
     *
     * @param fg name of the user's faba group
     * @param dn distinguishedName from the AD structure
     */
    public Person(String fg, String dn) {
        _fbGrp = fg;
        _orgUnit = findOrgUnits(dn);
        _name = getName(dn);
    }


    public String getName() {
        return _name;
    }

    /**
     * Gets the users name from the active directory schema
     *
     * @param input string of the Active directory common name
     * @return string with the user name
     */
    private String getName(String input) {
        return getMatchResults(input, "(CN\\=\\w+(\\s\\w+)*)", "(CN\\=)").get(0);
    }


    /**
     * Get the group where the users is member
     *
     * @param input string of the active directory memberOf
     * @return first group found in the string
     */
    private String findOrgUnits(String input) {
        return getMatchResults(input, "(OU\\=\\w+)", "(OU\\=)").get(0);
    }

    /**
     * This method compares a string with a regex expression and returns the strings that matched the regex.
     * After finding removes the part of the string unwanted
     *
     * @param input string with a line of the active directory schema
     * @param regex regular expression used to find the wanted information
     * @param exclude string composed with characters to be removed from the matched strings
     * @return list of the matched strings
     */
    private ArrayList<String> getMatchResults(String input, String regex, String exclude) {
        Pattern pattern4 = Pattern.compile(regex);
        Matcher matcher = pattern4.matcher(input);

        ArrayList<String> tempArr = new ArrayList<>();
        while(matcher.find())
        {
            String[] ous = matcher.group(0).replaceAll(exclude, "").split(",");
            for(String ou : ous) {
                tempArr.add(ou);
            }
        }

        return tempArr;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(_name, person._name) &&
                Objects.equals(_orgUnit, person._orgUnit) &&
                Objects.equals(_fbGrp, person._fbGrp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_name, _orgUnit, _fbGrp);
    }


    @Override
    public String toString() {
        return "Person{" +
                "_fbGrp='" + _fbGrp + '\'' +
                ", _name='" + _name + '\'' +
                ", _orgUnit='" + _orgUnit + '\'' +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        if( o instanceof Person ) {
            Person other = (Person) o;
            return this._name.compareTo(other._name);
        }
        return 0;
    }
}
