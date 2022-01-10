import SwiftUI
import TRIKOT_FRAMEWORK_NAME

extension VMDImageResource {
    public var image: Image? {
        if let imageName = TrikotViewModelDeclarative.shared.imageProvider.imageNameForResource(imageResource: self) {
            return Image(imageName)
        }
        return nil
    }
}