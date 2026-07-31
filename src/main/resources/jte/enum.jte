@import io.github.chrimle.classforge.enums.EnumModel
@import io.github.chrimle.classforge.enums.EnumModel.EnumConstantModel

@param EnumModel<?> enumModel

@if(enumModel.packageName() != null)
package ${enumModel.packageName()};
@endif

public enum ${enumModel.className()} {
@for(EnumConstantModel<?> enumConstant : enumModel.enumConstants())
   ${enumConstant.name()},
@endfor
}