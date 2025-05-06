# Adding Tips Screen with Navigation

## Steps

1. Add Navigation Dependency
   ```kotlin
   // In app/build.gradle.kts
   implementation("androidx.navigation:navigation-compose:2.7.7")
   ```

2. Create Navigation Routes
   ```kotlin
   object Routes {
       const val CALCULATOR = "calculator"
       const val TIPS = "tips"
   }
   ```

3. Create Tips Screen Placeholder
   ```kotlin
   @Composable
   fun TipsScreen(onBack: () -> Unit) {
       // Placeholder for tips content
       // Will be populated later with actual tips
   }
   ```

4. Modify MainActivity
   ```kotlin
   class MainActivity : ComponentActivity() {
       override fun onCreate(savedInstanceState: Bundle?) {
           super.onCreate(savedInstanceState)
           enableEdgeToEdge()
           setContent {
               val navController = rememberNavController()
               DistributionCalculatorTheme {
                   NavHost(
                       navController = navController,
                       startDestination = Routes.CALCULATOR
                   ) {
                       composable(Routes.CALCULATOR) {
                           Calculator(
                               onNavigateToTips = {
                                   navController.navigate(Routes.TIPS)
                               }
                           )
                       }
                       composable(Routes.TIPS) {
                           TipsScreen(
                               onBack = {
                                   navController.popBackStack()
                               }
                           )
                       }
                   }
               }
           }
       }
   }
   ```

5. Update Calculator's First Menu Item
   ```kotlin
   DropdownMenuItem(
       text = { Text("Tips") },
       onClick = {
           showMoreOptionsMenu = false
           onNavigateToTips()
       },
       leadingIcon = {
           Icon(
               Icons.Default.Info,
               contentDescription = null,
               tint = MaterialTheme.colorScheme.primary
           )
       }
   )
   ```

## Notes
- The Tips screen content will be added later as specified by the user
- Navigation is set up to allow easy addition of more screens in the future
- The Calculator screen remains the main/start screen
- The Tips screen will have a back button to return to the Calculator