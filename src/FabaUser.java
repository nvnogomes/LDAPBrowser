/**
 * Class description
 *
 * @author GomesNun
 * @version 201507.06001
 */
public class FabaUser extends Person {


    private boolean _enabled;

    private boolean _changed;


    public FabaUser(String fg, String dn) {
        super(fg, dn);
        _enabled = true;
        _changed = false;
    }

    public FabaUser(Person p) {
        _name = p._name;
        _fbGrp = p._fbGrp;
        _orgUnit = p._orgUnit;

        _changed = true;
        _enabled = true;
    }


    public boolean isDisabled() {
        return _enabled;
    }

    public void enable() {
        this._enabled = true;
    }

    public boolean changed() {
        return _changed;
    }

    public void UpdateUser(Person p) {
        _changed = true;
        _fbGrp = p._fbGrp;
        _orgUnit = p._orgUnit;
    }
}
