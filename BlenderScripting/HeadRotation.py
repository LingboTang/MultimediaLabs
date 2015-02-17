#----------------------------------------------------------
# File camera.py
# Adds one camera and several lights
#----------------------------------------------------------
import bpy, mathutils, math
from mathutils import Vector
from math import pi
from math import radians

 
def findMidPoint():
    sum = Vector((0,0,0))
    n = 0
    for ob in bpy.data.objects:
        if ob.type not in ['CAMERA', 'LAMP', 'EMPTY']:
            sum += ob.location
            n += 1
    if n == 0:
        return sum
    else:
        return sum/n
 
def addTrackToConstraint(ob, name, target):
    cns = ob.constraints.new('TRACK_TO')
    cns.name = name
    cns.target = target
    cns.track_axis = 'TRACK_NEGATIVE_Z'
    cns.up_axis = 'UP_Y'
    cns.owner_space = 'WORLD'
    cns.target_space = 'WORLD'
    return
 
def createLamp(name, lamptype, loc):
    bpy.ops.object.add(
        type='LAMP',
        location=loc)        
    ob = bpy.context.object
    ob.name = name
    lamp = ob.data
    lamp.name = 'Lamp'+name
    lamp.type = lamptype
    return ob
 
def createLamps(origin, target):
    deg2rad = 2*pi/360
 
    sun = createLamp('sun', 'SUN', origin+Vector((0,20,50)))
    lamp = sun.data
    lamp.type = 'SUN'
    addTrackToConstraint(sun, 'TrackMiddle', target)
 
    for ob in bpy.context.scene.objects:
        if ob.type == 'MESH':
            spot = createLamp(ob.name+'Spot', 'SPOT', ob.location+Vector((0,2,1)))
            bpy.ops.transform.resize(value=(0.5,0.5,0.5))
            lamp = spot.data
 
            # Lamp
            lamp.type = 'SPOT'
            lamp.color = (0.5,0.5,0)
            lamp.energy = 0.9
            lamp.falloff_type = 'INVERSE_LINEAR'
            lamp.distance = 7.5
 
            # Spot shape
            lamp.spot_size = 30*deg2rad
            lamp.spot_blend = 0.3
 
            # Shadows
            lamp.shadow_method = 'BUFFER_SHADOW'
            lamp.use_shadow_layer = True
            lamp.shadow_buffer_type = 'REGULAR'
            lamp.shadow_color = (0,0,1)
 
            addTrackToConstraint(spot, 'Track'+ob.name, ob)
    return
 
def createCamera(origin, target):
    # Create object and camera
    bpy.ops.object.add(
        type='CAMERA',
        location=origin,
        rotation=(pi/2,0,pi))        
    ob = bpy.context.object
    ob.name = 'MyCamOb'
    cam = ob.data
    cam.name = 'MyCam'
    addTrackToConstraint(ob, 'TrackMiddle', target)
 
 
    # Lens
    cam.type = 'PERSP'
    cam.lens = 75
    cam.lens_unit = 'MILLIMETERS'
    cam.shift_x = -0.05
    cam.shift_y = 0.1
    cam.clip_start = 10.0
    cam.clip_end = 250.0
 
    empty = bpy.data.objects.new('DofEmpty', None)
    empty.location = origin+Vector((0,10,0))
    cam.dof_object = empty
 
    # Display
    cam.show_title_safe = True
    cam.show_name = True
 
    # Make this the current camera
    scn = bpy.context.scene
    scn.camera = ob
    return ob


# http://stackoverflow.com/questions/16189503/blender-camera-rotation-with-python-not-planar
def parent_obj_to_camera(b_obj, b_camera):
    origin = (0,0,0) #can be replaced with b_obj.location
    b_empty = bpy.data.objects.new("Empty", None)
    b_empty.location = origin
    b_camera.parent = b_empty #setup parenting

    scn = bpy.context.scene
    scn.objects.link(b_empty)
    scn.objects.active = b_empty 
    b_empty.select = True
    
    num_steps = 1147
    stepsize = 360/num_steps
    for i in range(0, num_steps):
        mat_rot = mathutils.Matrix.Rotation(radians(step), 4, 'Z')
        b_empty.matrix_local *= mat_rot 

        print("Rotation %01d" % (radians(stepsize)))
        image = 'images/' + sys.argv[-1] + str(i) + '.' + filetype
        render_thumb(image,gl=False)
    return
 
def run(origin):
    # Delete all old cameras and lamps
    scn = bpy.context.scene
    for ob in scn.objects:
        if ob.type == 'CAMERA' or ob.type == 'LAMP':
            scn.objects.unlink(ob)
 
    # Add an empty at the middle of all render objects
    midpoint = findMidPoint()
    bpy.ops.object.add(
        type='EMPTY',
        location=midpoint),
    target = bpy.context.object
    target.name = 'Target'
 
    
    createLamps(origin, target)
    b_camera = createCamera(origin+Vector((5,5,20)), target)
    b_empty = bpy.context.scene.active_object
    parent_obj_to_camera(b_empty,b_camera)
    return
 
if __name__ == "__main__":
    run(Vector((0,0,0)))
