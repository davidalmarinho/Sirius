package physics2d_from_scratch.forces;

import physics2d_from_scratch.rigidBody.RigidBody2D;

import java.util.ArrayList;
import java.util.List;

public class ForceRegistry {
    private List<ForceRegistration> registryList;

    public ForceRegistry() {
        this.registryList = new ArrayList<>();
    }

    public void add(RigidBody2D rb, IForceGenerator fg) {
        ForceRegistration forceRegistration = new ForceRegistration(fg, rb);
        registryList.add(forceRegistration);
    }

    public void remove(RigidBody2D rigidBody2D, IForceGenerator iForceGenerator) {
        ForceRegistration forceRegistration = new ForceRegistration(iForceGenerator, rigidBody2D);
        registryList.remove(forceRegistration);
    }

    public void clear() {
        registryList.clear();
    }

    /**
     * Updates each force
     * @param dt Delta time per tick
     */
    public void updateForces(float dt) {
        for (ForceRegistration forceRegistration : registryList) {
            forceRegistration.forceGenerator.update(forceRegistration.rigidBody2D, dt);
        }
    }

    public void zeroForces() {
        for (ForceRegistration forceRegistration : registryList) {
            // TODO: 30/01/2022 Implement me
            // forceRegistration.rigidBody2D.zeroForces();
        }
    }
}
