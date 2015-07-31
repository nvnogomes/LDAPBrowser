import java.util.ArrayList;

/**
 * Class description
 *
 * @author GomesNun
 * @version 201507.03001
 */
public class Group {

    private ArrayList<Person> _adUsers;
    private String _name;


    public Group(String _name) {
        this._name = _name;
        _adUsers = new ArrayList<>();
    }


    public void setUsers(String[] userList) {
        for (int i = 0; i < userList.length; i++) {
            _adUsers.add(new Person(_name, userList[i]));
        }
    }


    @Override
    public String toString() {
        return "Group{" +
                "_name='" + _name + '\'' +
                ", _adUsers=" + _adUsers +
                '}';
    }
}
