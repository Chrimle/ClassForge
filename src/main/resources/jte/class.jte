@import io.github.chrimle.classforge.classes.ClassModel

@param ClassModel classModel

@if(classModel.packageName() != null)
package ${classModel.packageName()};
@endif

public class ${classModel.className()} {

}