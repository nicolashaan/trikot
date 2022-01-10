import SwiftUI
import Trikot_viewmodels_declarative
import TrikotViewmodelsDeclarativeSample

struct HomeView: RootViewModelView {
    typealias VM = HomeViewModel

    @ObservedObject var observableViewModel: ObservableViewModelAdapter<HomeViewModel>

    var viewModel: HomeViewModel {
        return observableViewModel.viewModel
    }

    init(viewModel: HomeViewModel) {
        self.observableViewModel = viewModel.asObservable()
    }

    var body: some View {
        NavigationView {
            VMDList(viewModel.items) { item in
                VMDButton(item) { textContent in
                    Text(textContent.text)
                }
                .foregroundColor(.black)
            }
            .listStyle(GroupedListStyle())
            .navigationTitle(viewModel.title.text)
        }
    }
}

struct HomeViewPreviews: PreviewProvider {
    static var previews: some View {
        return PreviewView {
            HomeView(viewModel: HomeViewModelPreview())
        }
    }
}
