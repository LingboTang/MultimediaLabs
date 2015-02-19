import bpy, mathutils, math
from mathutils import *
from math import *
 
def addTrackToConstraint(ob, name, target):
    cns = ob.constraints.new('TRACK_TO')
    cns.name = name
    cns.target = target
    cns.subtarget = 'Head'
    cns.track_axis = 'TRACK_NEGATIVE_Z'
    cns.up_axis = 'UP_Y'
    cns.owner_space = 'WORLD'
    cns.target_space = 'WORLD'
    return
 
def createCamera(origin, target):
    # Create object and camera
    bpy.ops.object.add(
        type='CAMERA',
        location=origin,
        rotation=(0,0,0))        
    ob = bpy.context.object
    ob.name = 'MyCamOb'
    cam = ob.data
    cam.name = 'MyCam'
    addTrackToConstraint(ob, 'TrackMiddle', target)
 
 
    # Lens
    cam.type = 'PERSP'
    cam.lens = 150
    cam.lens_unit = 'MILLIMETERS'
    cam.shift_x = -0.05
    cam.shift_y = 0.1
    cam.clip_start = 0.0
    cam.clip_end = 250.0
 
    
    # Display
    cam.show_title_safe = True
    cam.show_name = True
 
    # Make this the current camera
    scn = bpy.context.scene
    scn.camera = ob
    return ob
 
def run(origin):
    # Delete all old cameras and lamps
    scn = bpy.context.scene
    for ob in scn.objects:
        if ob.type == 'CAMERA' or ob.type == 'LAMP':
            scn.objects.unlink(ob)
 
    # Add an empty at the middle of all render objects
    
    skel_obj= bpy.data.objects['131_09_60fps']
    bpy.ops.object.add(
        type='EMPTY',
        location=origin),
    target = skel_obj
    target.name = 'Target'
    b_cam = createCamera(origin+Vector((0,0,0)), target)
    frame_num = 0
    x_radians = 0
    y_radians = 0
    z_radians = 0
    for i in range(1148):
        bpy.context.scene.frame_set(frame_num)
        z_radians += float(2*pi/1147)
        b_cam.rotation_euler =(x_radians,y_radians,z_radians)
        bpy.ops.anim.keyframe_type(type='Rotation',confirm_success=True)
        frame_num+=1
    return
 
if __name__ == "__main__":
    run(Vector((0,0,0)))
