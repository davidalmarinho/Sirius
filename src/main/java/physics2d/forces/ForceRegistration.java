package physics2d.forces;

import physics2d.rigidBody.RigidBody2D;

public class ForceRegistration {
    public IForceGenerator forceGenerator;
    public RigidBody2D rigidBody2D;

    public ForceRegistration(IForceGenerator forceGenerator, RigidBody2D rigidBody2D) {
        this.forceGenerator = forceGenerator;
        this.rigidBody2D = rigidBody2D;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass() != ForceRegistration.class) return false;

        ForceRegistration forceRegistration = (ForceRegistration) o;
        return forceRegistration.forceGenerator == this.forceGenerator
                && forceRegistration.rigidBody2D == this.rigidBody2D;
    }
}
