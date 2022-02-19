## Architecture
The purpose of this document is to explain how the code in this app is structured and basically how things should be done. The main focus is on the most complex aspect of app development which is the presentation architecture, that is to say how we go from a network response to putting something on the screen and how interactions with the user are treated by the system.

### Network Requests
As of writing this, all our network requests use OKHttp/Retrofit. In the future, we may look into using ktor for this. Every HTTP network call should be written as a `suspend fun` on a retrofit interface. These methods will exist in a file titled `*ApiService.kt` where the name of the file matches the retrofit interface. Each method should return a retrofit `Response<T>` so that network errors are wrapped in the returning object and exceptions are not thrown in bad response type flows (or lack of network). The object type `T` returned in the `Response<T>` should be a near exact replica of the actual JSON payload returned from the API in question. This is not the place to make changes like nullability or changing something to an enum. Simply represent the JSON itself in Kotlin data classes.

Network requests in a `*ApiService` should contain all the potential parameters necessary for all uses of the retrofit call. Additional methods should not be created for different versions of the same request. Populating the correct parameters is handled at the service layer. You should attempt to use as few abstractions as possible when writing api services. Represent everything as what it is, a network request, nothing more, nothing less. 

### Services
Moving up a level of abstraction from network requests are Services. Services are loosely defined as something that fetches business objects. This could use the network, a database, or something else. Services have no explicit knowledge of the underlying mechanism for fetching their data and exist to close the gap between something like a network request and loading a real object the app can use.

Where Api Services were not the place for abstractions or changes to the data returned elsewhere, this very much is. If an Api Service is returning something that is better represented by an enum, then convert it at the Service layer. The result of the service layer is not an object but rather a `Flow<T>` This entire app leans heavily on Functional Reactive Programming and Kotlin Flows. Since you are returning a `Flow<T>` there's no real need to worry about the fact that networking calls are `suspend fun`s. A flow can simply be constructed with something like `flow { ... }` and call the `suspend fun`s as if they are normal sequential non coroutine code.

Concepts such as polling can also be easily implemented with a `Flow<T>` that calls a retrofit service in a loop. It is at the service layer that deconstructing a `Response<T>` should occur and the appropriate errors (as business objects) sent along the flow. *Do not allow an exception to be thrown unless we are hitting a truly unexpected and unrecoverable error*. This does not mean eating exceptions but rather not throwing them when something like a network failure occurs which is a normal expected permutation on the app flow. 

Since a service returns a Flow. It can return more than one object. This makes it possible for your `Flow<T>` to return a `Loading` type object followed by the real object once it has loaded. 

It's important to note that what a Service really returns are events. These events are most often transitions that mutate the current screen state. For for instance, a Service for pattern details might return something like `PatternLoaded` in its `Flow<T>` Note that this does not represent the entire screen state, but just something that would change the current screen state.

If you run into a situation where you need more than a single request to load a Screen State. The Service layer is the place to do this as you can simply construct multiple flows and merge their results. Ensure that flows are merged and not concatenated and that code is written for the possibility that they come in any order.

### Content State
Content state represents the state of the main "content" of the screen. This excludes things like navigation bars or the title bar. Just the main content of the screen. An implementation of content state takes one or more services and returns `State` from the events emitted. Essentially, this is a type of State Machine.

State does not exclusively have to come from services. Some state can simply be obtained from the OS (nearly instantly) or from data passed in. However, the Content State returns everything necessary to render the content of a page on OpenStitch. Generally the use of Flow's `scan(...)` function is the preferred method for state machine implementation.

### Types of Content State
Content States come in a couple different types. Technically a content state could just be something that has `@Composable` code that can be generated from it, however in practice most screens share some general architecture. For instance, apps open have list view and detail views. Therefore ContentStates can implement some common interfaces to describe their behavior and give them structure. 

#### ListState
This is a type of ContentState that represents a List in OpenStitch. Lists are generally supposed to show a list of business objects to the user that they can scroll through and perform actions on. Therefore making your `ContentState` a `ListState` enforces that you implement a `List<ListItemState` which is a business object state representation of a `List`. Each `ListItemState` should have all the information necessary to render that list item and respond to all `ViewEvents` emitted by the underlying `@Composable`. When events come from the List View they will have positions and no other data. Therefore, it is vital that this list of items be able to map positional data to state relevant to the `ViewEvent`. This state should also be updated with any filtering or sorting of the list making it easy to always know what a click on position x actually represents without having to calculate or yank that from state elsewhere (this is a big source of bugs).

#### DetailState
Detail state represents a ContentState that is showing a single item's details.

### ViewState
ViewState is just a transformation of State into another object containing only what is absolutely necessary to render a `@Composable` function. Certain ViewStates may define exactly what function signiture should be used for `@Composable`s. In situations like a ListView where every item needs to be able to emit some of the same view events such as "Clicks" or "Swipes" this enforces that we have a singular way to do this. 

The `ViewState` itself keeps a layer between business objects and the `@Composable` code that renders views. The real reason we use them vs just using `State` inside our `@Composables` is to keep logic out of places that are hard to test. a `Map` function that turns `State` into `ViewState` is easier to test than a `@Composable` that looks at state and evaluates how the UI should show. Therefore in `ViewState` it makes a lot of sense to refer to view concepts directly like whether something is visible or not. If you have a badge with two lines, consider making the `ViewState` having 

```
data class MyViewState(
  val topLineText: String,
  val bottomLineText: String,
) : ViewState
```

as opposed to something like pattern name and pattern price. The `ViewState` exists to let us "dumb down" what goes on in `@Composable` functions as much as possible.


### Navigation

### Cacheing





